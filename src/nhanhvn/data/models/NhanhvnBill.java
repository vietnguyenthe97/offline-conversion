package nhanhvn.data.models;

public class NhanhvnBill {
    private String id;
    private String customerName;
    private String customerMobile;
    private double money;
    private NhanhvnBillProductDetail products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public NhanhvnBillProductDetail getProducts() {
        return products;
    }

    public void setProducts(NhanhvnBillProductDetail products) {
        this.products = products;
    }
}

