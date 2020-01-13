package nhanhvn.rest.api;

public class BillData extends AbstractData{
	public BillData() {
		this.setUrl("https://graph.nhanh.vn/api/bill/search");
		this.initialize();
	}
}
