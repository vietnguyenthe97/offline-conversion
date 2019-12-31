package nhanhvn.data.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class NhanhvnBill {
	@Expose
    private String id;
	@Expose
    private String customerName;
	@Expose
    private String customerMobile;
	@Expose
    private double money;
	@Expose
	private String createdDateTime;
    private List<NhanhvnBillProductDetail> products = new ArrayList<>();

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

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setProducts(List<NhanhvnBillProductDetail> products) {
		this.products = products;
	}
}

