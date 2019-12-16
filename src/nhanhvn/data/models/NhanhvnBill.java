package nhanhvn.data.models;

import java.util.List;

import com.google.gson.annotations.Expose;

public class NhanhvnBill {
	@Expose
    private String id;
	@Expose
    private String customerName;
	@Expose
    private String customerMobile;
	@Expose
    private double money;
    private List<NhanhvnBillProductDetail> products;

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

	public List<NhanhvnBillProductDetail> getProducts() {
		return products;
	}

	public void setProducts(List<NhanhvnBillProductDetail> products) {
		this.products = products;
	}
}

