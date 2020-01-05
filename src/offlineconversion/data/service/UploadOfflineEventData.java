package offlineconversion.data.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.rest.api.CustomResponseHandler;
import nhanhvn.security.apistorage.ApiCredentials;
import nhanhvn.security.apistorage.ApiHelper;
import offlineconversion.data.models.ConversionData;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
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
import java.util.stream.Collectors;

public class UploadOfflineEventData {
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

    private void initializeBills() throws SQLException {
        DatabaseConnection db = new DatabaseConnection();
        bills = db.getBillsFromDb().getNhanhvnBillList().stream()
                .filter(bill -> {
                    Date createdDateTime = DataHelper.parseDateTimeString(bill.getCreatedDateTime());
                    return DataHelper.isDateWithin62DaysUntilToday(createdDateTime);
                })
                .collect(Collectors.toList());
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

    private void uploadBill(JsonArray dataJsonArray, String id) throws IOException {
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

        ResponseHandler<String> responseHandler = CustomResponseHandler.createResponseHandler();
        HttpResponse response = null;

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
            System.out.println("Successfully uploaded bill: " + id);
            System.out.println(response.getEntity());
            EntityUtils.consumeQuietly(response.getEntity());
        } else {
            System.out.println("Unexpected error: " + response.getStatusLine().getStatusCode());
        }
        System.out.println("Total bills: " + bills.size());
    }

    public void uploadAllBills() throws SQLException {
        initializeHttpRequest();
        System.out.println("Filtering bills within previous 62 days (some bills may expire)");
        initializeBills();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("resources/filterbill.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        System.out.println("Total bills: " + bills.size());
        printWriter.printf("Total bills filtered: %s\n" , bills.size());

        bills.stream().forEach(bill -> {
            DataConverter dataConverter = new DataConverter();
            try {
                dataConverter.prepareSendingData(bill);
                ConversionData data = dataConverter.getConversionData();
                JsonArray dataArray = prepareHttpPostParameters(data);
                //uploadBill(dataArray, bill.getId());
                System.out.println("Bill id is: "  + bill.getId());
                printWriter.printf("Bill id: %s\n", bill.getId());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
        printWriter.close();
    }

    private void addParam(String name, String value) {
        Objects.requireNonNull(postParams);
        postParams.add(new BasicNameValuePair(name, value));
    }

    public static void main(String[] args) throws IOException, SQLException {
        UploadOfflineEventData data = new UploadOfflineEventData();
        data.uploadAllBills();
    }

}
