package offlineconversion.data.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import nhanhvn.security.apistorage.ApiCredentials;
import nhanhvn.security.apistorage.ApiHelper;
import offlineconversion.data.models.ConversionData;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import shared.datahelper.DataHelper;
import shared.persistence.DatabaseConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UploadOfflineEventService {
    private List<NhanhvnBill> bills;
    private ApiCredentials apiCredentials;
    private CloseableHttpClient httpClient;
    private List<NameValuePair> postParams;
    private List<NameValuePair> headerParams;
    private HttpPost httpPost;

    /**
     * CONTENT PARAMS
     */
    private final String API_VERSION = "version";
    private final String OAUTH2_TOKEN = HttpHeaders.AUTHORIZATION;
    private final String MATCH_KEYS = "match_keys";
    private final String PHONE = "phone";
    private final String CURRENCY = "currency";
    private final String VALUE = "value";
    private final String EVENT_NAME = "event_name";
    private final String EVENT_TIME = "event_time";
    private final String CONTENTS = "contents";

    /**
     *  HTTP BODY POST PARAMS
     */
    private final String DATA = "data";
    private final String UPLOAD_TAG = "upload_tag";

    public List<NhanhvnBill> getBills() {
        return bills;
    }

    private void initializeHttpRequest() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(28800)
                .setConnectTimeout(28800)
                .setSocketTimeout(28800)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnPerRoute(4)
                .setMaxConnTotal(4)
                .build();

        apiCredentials = ApiHelper.getApiCredentials();
        postParams = new ArrayList<>();
        headerParams = new ArrayList<>();
        headerParams.add(new BasicNameValuePair(API_VERSION, apiCredentials.getFacebookDetails().getVersion()));
        headerParams.add(new BasicNameValuePair(OAUTH2_TOKEN,
                "Bearer " + apiCredentials.getFacebookDetails().getUserAccessToken()));
    }

    private void addParam(String name, String value) {
        Objects.requireNonNull(postParams);
        postParams.add(new BasicNameValuePair(name, value));
    }

    public void initializeBills() throws SQLException {
        DatabaseConnection db = new DatabaseConnection();
        bills = db.getBillsFromDb().getNhanhvnBillList().stream()
                .filter(bill -> {
                    Date createdDateTime = DataHelper.parseDateTimeString(bill.getCreatedDateTime());
                    return DataHelper.isDateWithin62DaysUntilToday(createdDateTime);
                })
                .collect(Collectors.toList());

        bills = filterBillsWithNoUnmatchedProducts(bills);
        System.out.println("Bill size: " + bills.size());
    }

    private List<NhanhvnBill> filterBillsWithNoUnmatchedProducts(List<NhanhvnBill> bills) {
        List<NhanhvnBill> billList;
        //filter all bills that have a product with id = 0
        billList = bills.stream().filter(e -> e.getProducts().stream()
                        .allMatch(productDetail -> !productDetail.getFacebookId().equals("0")))
                .collect(Collectors.toList());

        //filter all products that have id -1
        billList.stream().forEach(bill -> {
            List<NhanhvnBillProductDetail> products = bill.getProducts().stream()
                    .filter(product -> !product.getFacebookId().equals("-1"))
                    .collect(Collectors.toList());
            bill.setProducts(products);
        });

        //filter all bills that have no product
        billList = billList.stream().filter(bill -> bill.getProducts().size() > 0).collect(Collectors.toList());
        return billList;
    }

    private JsonArray prepareHttpPostParameters(ConversionData data) {
        Gson gson = new GsonBuilder().create();
        List<String> hashedPhones = new ArrayList<>();
        hashedPhones.add(data.getMatchKey());
        JsonArray hashedPhoneArray = gson.toJsonTree(hashedPhones).getAsJsonArray();

        JsonObject matchKeys = new JsonObject();
        matchKeys.add(PHONE, hashedPhoneArray);

        JsonArray contentsArray = gson.toJsonTree(data.getContents().getContentElements()).getAsJsonArray();

        JsonObject allData = new JsonObject();
        allData.add(MATCH_KEYS, matchKeys);
        allData.addProperty(CURRENCY, data.getCurrency());
        allData.addProperty(VALUE, data.getValue());
        allData.addProperty(EVENT_NAME, "Purchase");
        allData.addProperty(EVENT_TIME, data.getEventTime());
        allData.add(CONTENTS, contentsArray);

        JsonArray finalArray = new JsonArray();
        finalArray.add(allData);
        return finalArray;
    }

    private void uploadBill(JsonArray dataJsonArray, NhanhvnBill bill) throws IOException, SQLException {
        String version = apiCredentials.getFacebookDetails().getVersion();
        String offlineEventSetId = apiCredentials.getFacebookDetails().getOfflineEventSetId();
        final String url = "https://graph.facebook.com/" + version + "/" + offlineEventSetId + "/events";
        this.httpPost = new HttpPost(url);

        httpPost.addHeader(headerParams.get(0).getName(), headerParams.get(0).getValue());
        httpPost.addHeader(headerParams.get(1).getName(), headerParams.get(1).getValue());

        System.out.println("Data array: " + dataJsonArray);
        this.addParam(UPLOAD_TAG, "in-store uploads");
        this.addParam(DATA, dataJsonArray.toString());

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, Consts.UTF_8);
        httpPost.setEntity(entity);
        HttpResponse response;

        final int MAX_RETRY = 10;
        int retry = 0;
        while(true) {
            try {
                response = httpClient.execute(httpPost);
                break;
            } catch (IOException e) {
                if(e instanceof NoHttpResponseException) {
                    retry++;
                    System.out.println("Retrying + " + retry + " time(s)...");
                    if(retry == MAX_RETRY) {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            System.out.println("Successfully uploaded bill: " + bill.getId());
            DatabaseConnection databaseConnection = new DatabaseConnection();
            bill.setFacebookStatus(true);
            System.out.println("Bill upload status: " + bill.getFacebookStatus());
            databaseConnection.updateBillUploadStatus(bill);
            System.out.println(response.getEntity());
            EntityUtils.consumeQuietly(response.getEntity());
        } else {
            System.out.println("Unexpected error: " + response.getStatusLine().getStatusCode());
        }
    }

    public void uploadAllBills() throws SQLException {
        AtomicInteger alreadyUploadedBills = new AtomicInteger();
        initializeHttpRequest();
        System.out.println("Filtering bills within previous 62 days (some bills may expire)");
        initializeBills();

        FileWriter uploadFileWriter = null;
        FileWriter uploadBeforeFileWriter = null;
        FileWriter totalFilteredBillsFileWriter = null;

        try {
            uploadFileWriter = new FileWriter("resources/uploaded_bills.txt");
            uploadBeforeFileWriter = new FileWriter("resources/uploaded_before_bills.txt");
            totalFilteredBillsFileWriter = new FileWriter("resources/filtered_bill.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter uploadPrintWriter = new PrintWriter(uploadFileWriter);
        PrintWriter uploadBeforePrintWriter = new PrintWriter(uploadBeforeFileWriter);
        PrintWriter totalFilteredBillsPrintWriter = new PrintWriter(totalFilteredBillsFileWriter);

        System.out.println("Total bills: " + bills.size());
        totalFilteredBillsPrintWriter.printf("Total bills filtered: %s\n" , bills.size());

        bills.stream().forEach(bill -> {
            DataConverter dataConverter = new DataConverter();
            try {
                dataConverter.prepareSendingData(bill);
                ConversionData data = dataConverter.getConversionData();
                JsonArray dataArray = prepareHttpPostParameters(data);
                DatabaseConnection databaseConnection = new DatabaseConnection();
                NhanhvnBill inspectBill = databaseConnection.getBillById(bill.getId());

                if (inspectBill.getFacebookStatus()) {
                    alreadyUploadedBills.getAndIncrement();
                    System.out.println("Already updated bill: " + alreadyUploadedBills.get());
                    System.out.println("Bill has been updated!");
                    System.out.println("Upload event status: " + inspectBill.getFacebookStatus());
                    uploadBeforePrintWriter.printf("Bill already uploaded: %s, bill upload facebook status: %s\n",
                            inspectBill.getId(),
                            inspectBill.getFacebookStatus());
                    uploadBeforePrintWriter.flush();
                } else {
                    uploadBill(dataArray, bill);
                    uploadPrintWriter.printf("Bill has just been uploaded: %s, bill upload facebook status before: %s\n",
                            bill.getId(),
                            inspectBill.getFacebookStatus());
                    uploadPrintWriter.flush();
                }

                System.out.println("Bill id is: "  + bill.getId());
                totalFilteredBillsPrintWriter.printf("Bill id: %s\n", bill.getId());
                totalFilteredBillsPrintWriter.printf("Created date: %s\n", bill.getCreatedDateTime());
                totalFilteredBillsPrintWriter.printf("Customer mobile: %s\n", bill.getCustomerMobile());
                totalFilteredBillsPrintWriter.printf("Bill price: %s\n", bill.getMoney());
                totalFilteredBillsPrintWriter.printf("Product in bill : %s\n", bill.getProducts().size());
                totalFilteredBillsPrintWriter.printf("Customer name: %s\n======\n", bill.getCustomerName());
                bill.getProducts().stream().forEach(productDetail -> {
                    totalFilteredBillsPrintWriter.printf("Product quantity: %s\n", productDetail.getQuantity());
                    totalFilteredBillsPrintWriter.printf("Product bill id: %s\n", productDetail.getBillId());
                    totalFilteredBillsPrintWriter.printf("Product facebook id: %s\n", productDetail.getFacebookId());
                    totalFilteredBillsPrintWriter.printf("Product price: %s\n", productDetail.getPrice());
                    totalFilteredBillsPrintWriter.printf("Product id: %s\n======\n", productDetail.getId());
                });
                totalFilteredBillsPrintWriter.printf("----------------------------------------------\n\n");
                totalFilteredBillsPrintWriter.flush();
            } catch (NoSuchAlgorithmException | SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        totalFilteredBillsPrintWriter.printf("Bills already uploaded: %s\n", alreadyUploadedBills.get());
        totalFilteredBillsPrintWriter.printf("New bills uploaded: %s\n", bills.size() - alreadyUploadedBills.get());
        totalFilteredBillsPrintWriter.printf("Total bills uploaded before: %s", alreadyUploadedBills.get());
        totalFilteredBillsPrintWriter.close();

        uploadPrintWriter.printf("New bills uploaded: %s", bills.size() - alreadyUploadedBills.get());
        uploadPrintWriter.close();

        uploadBeforePrintWriter.printf("Total bills uploaded before: %s", alreadyUploadedBills.get());
        uploadBeforePrintWriter.close();
    }
}
