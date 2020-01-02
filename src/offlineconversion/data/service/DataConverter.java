package offlineconversion.data.service;

import com.google.gson.*;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import offlineconversion.data.models.ContentElements;
import offlineconversion.data.models.Contents;
import offlineconversion.data.models.ConversionData;
import shared.datahelper.DataHelper;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DataConverter {
    private ConversionData conversionData;

    public DataConverter() {
        conversionData = new ConversionData();
    }

    public ConversionData getConversionData() {
        return conversionData;
    }

    public void prepareSendingData(NhanhvnBill bill)
            throws NoSuchAlgorithmException {
        Objects.requireNonNull(conversionData, "ConversionData must not be null!");
        Objects.requireNonNull(bill, "NhanhvnBill must not be null!");

        conversionData.setUploadTag("in-store");
        conversionData.setCurrency("VND");
        conversionData.setEventName("Purchase");

        String createdDateTimeString = bill.getCreatedDateTime();
        Date createdDateTime = DataHelper.parseDateTimeString(createdDateTimeString);
        String unixTimeStamp = DataHelper.convertDateToUnixTimeStampString(createdDateTime);
        conversionData.setEventTime(unixTimeStamp);

        conversionData.setValue(bill.getMoney());

        prepareContents(conversionData.getContents(), bill);

        String rawPhoneNumber = DataHelper.formatMobileNumber(bill.getCustomerMobile());
        String hashedPhoneNumber = DataHelper.sha256Hash(rawPhoneNumber);
        conversionData.setMatchKey(hashedPhoneNumber);
    }

    private void prepareContents(Contents contents, NhanhvnBill bill) {
        Objects.requireNonNull(contents, "Contents must not be null!");
        Objects.requireNonNull(bill, "NhanhvnBill must not be null!");

        List<NhanhvnBillProductDetail> products = bill.getProducts();
        products.stream().forEach(product -> {
            ContentElements contentElements = new ContentElements();
            contentElements.setQuantity(product.getQuantity());
            contentElements.setId(product.getFacebookId());
            contents.getContentElements().add(contentElements);
        });
    }

    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
//        DatabaseConnection db = new DatabaseConnection();
//        DataConverter dataConverter = new DataConverter();

//
//        dataConverter.prepareSendingData(bills.get(0));
//
//        int size = dataConverter.getConversionData().getContents().getContentElements().size();
//        List<ContentElements> contentElements = dataConverter.getConversionData().getContents().getContentElements();
//        System.out.println(size);
//        System.out.println(dataConverter.getConversionData().getMatchKey());
//        System.out.println(dataConverter.getConversionData().getCurrency());
//        System.out.println(dataConverter.getConversionData().getEventName());
//        System.out.println(dataConverter.getConversionData().getEventTime());
//        System.out.println(dataConverter.getConversionData().getValue());
//        for (int i=0; i<size; i++) {
//            System.out.println("Fbid: " + contentElements.get(i).getId());
//            System.out.println("Qty: " + contentElements.get(i).getQuantity());
//        }

        Gson gson = new GsonBuilder().create();
        List<ContentElements> contentElements = new ArrayList<>();
        contentElements.add(new ContentElements("abc",1));
        contentElements.add(new ContentElements("def",2));
        contentElements.add(new ContentElements("egh",3));


        List<String> phones = new ArrayList<>();
        phones.add("asdhjasiofsioafsaopf");
        JsonArray phoneArray = gson.toJsonTree(phones).getAsJsonArray();

        JsonObject phone = new JsonObject();
        phone.add("phone", phoneArray);

        JsonArray contentJsonArray = gson.toJsonTree(contentElements).getAsJsonArray();

        JsonObject allData = new JsonObject();
        allData.add("match_keys", phone);
        allData.addProperty("currency", "VND");
        allData.addProperty("value", 75000);
        allData.addProperty("event_name", "Purchase");
        allData.addProperty("event_time", "1571818457");
        allData.add("contents", contentJsonArray);

        JsonArray finalArray = new JsonArray();
        finalArray.add(allData);

        System.out.println(finalArray.toString());

    }
}