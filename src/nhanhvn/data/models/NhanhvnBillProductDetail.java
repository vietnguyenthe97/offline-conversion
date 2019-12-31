package nhanhvn.data.models;

import com.google.gson.annotations.Expose;

public class NhanhvnBillProductDetail {
    @Expose
    private String id;

    @Expose
    private float quantity;

    @Expose
    private double price;

    private String billId;

    private String facebookId;

    public String getId() {
        return id;
    }

    public NhanhvnBillProductDetail() {

    }

    public NhanhvnBillProductDetail(NhanhvnBillProductDetail nhanhvnBillProductDetail) {
        this.id = nhanhvnBillProductDetail.getId();
        this.quantity = nhanhvnBillProductDetail.getQuantity();
        this.price = nhanhvnBillProductDetail.getPrice();
        this.billId = nhanhvnBillProductDetail.getBillId();
        this.facebookId = nhanhvnBillProductDetail.getFacebookId();
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
