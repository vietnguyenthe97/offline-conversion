package offlineconversion.data.service;

import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import offlineconversion.data.models.ContentElements;
import offlineconversion.data.models.Contents;
import offlineconversion.data.models.ConversionData;
import shared.datahelper.DataHelper;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DataConverter {
    public static ConversionData prepareSendingData(ConversionData conversionData, NhanhvnBill bill)
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
        String hashedPhoneNumber = DataHelper.Sha256Hash(rawPhoneNumber);
        conversionData.setMatchKey(hashedPhoneNumber);

        return conversionData;
    }

    public static void prepareContents(Contents contents, NhanhvnBill bill) {
        Objects.requireNonNull(contents, "Contents must not be null!");
        Objects.requireNonNull(bill, "NhanhvnBill must not be null!");

        List<NhanhvnBillProductDetail> products = bill.getProducts();
        products.stream().forEach(product -> {
            ContentElements contentElements = new ContentElements();
            contentElements.setQuantity(product.getQuantity());
            contentElements.setFacebookId(product.getFacebookId());
            contents.getContentElements().add(contentElements);
        });
    }

    public static void main(String[] args) {
        ConversionData data = new ConversionData();
        DataConverter converter = new DataConverter();

        System.out.println(data.getMatchKey());
    }
}
