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
}