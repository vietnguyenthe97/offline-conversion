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
import shared.persistence.DatabaseConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
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
        bills = db.getBillsFromDb().getNhanhvnBillList().stream().collect(Collectors.toList());
        bills = filterBillsWithNoUnmatchedProducts(bills);
    }

    private List<NhanhvnBill> filterBillsWithNoUnmatchedProducts(List<NhanhvnBill> bills) {
        List<NhanhvnBill> billList = new ArrayList<>();
        FileWriter deletedBills = null;
        try {
            deletedBills = new FileWriter("resources/deleted_bills.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter billWriter = new PrintWriter(deletedBills);
        billWriter.printf("Bills within 62 days and has facebookStatus = 0 (not uploaded): %s\n", bills.size());

        for (NhanhvnBill bill: bills) {
            for (int i=0; i<bill.getProducts().size(); i++) {
                if (bill.getProducts().get(i).getFacebookId().equals("0")) {
                    billWriter.printf("Bill %s is removed because product has facebookid = 0\n", bill.getId());
                    break;
                }

                if (i == bill.getProducts().size()-1) {
                    billList.add(bill);
                }
            }
        }
        billWriter.printf("Bills after removing products with facebookid = 0: %s\n", billList.size());
        System.out.println("Bills after removing products with facebookid = 0: " +  billList.size());

        billList.stream().forEach(bill -> {
            List<NhanhvnBillProductDetail> products = bill.getProducts().stream()
                    .filter(product -> !product.getFacebookId().equals("-1"))
                    .collect(Collectors.toList());
            bill.setProducts(products);
        });

        List<NhanhvnBill> finalList = new ArrayList<>();
        for (NhanhvnBill bill: billList) {
            if (bill.getProducts().size() > 0) {
                finalList.add(bill);
            } else {
                billWriter.printf("Bill %s is removed because bill has no product\n", bill.getId());
            }
        }
        billWriter.printf("Final bills after moving bills with no product: %s\n", finalList.size());

        return finalList;
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
            databaseConnection.updateBillUploadStatus(bill);
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

        FileWriter totalFilteredBillsFileWriter = null;

        try {
            totalFilteredBillsFileWriter = new FileWriter("resources/filtered_bill.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter totalFilteredBillsPrintWriter = new PrintWriter(totalFilteredBillsFileWriter);

        System.out.println("Total bills: " + bills.size());

        bills.stream().forEach(bill -> {
            DataConverter dataConverter = new DataConverter();
            try {
                dataConverter.prepareSendingData(bill);
                ConversionData data = dataConverter.getConversionData();
                JsonArray dataArray = prepareHttpPostParameters(data);

                uploadBill(dataArray, bill);
                totalFilteredBillsPrintWriter.printf("Bill uploaded: %s\n", bill.getId());

                System.out.println("Bill uploaded: "  + bill.getId());
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
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        totalFilteredBillsPrintWriter.printf("New bills uploaded: %s", bills.size());
        totalFilteredBillsPrintWriter.close();
    }
}
