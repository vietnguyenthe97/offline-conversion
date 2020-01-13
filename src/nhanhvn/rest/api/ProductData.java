package nhanhvn.rest.api;

public class ProductData extends AbstractData{
	public ProductData() {
		this.setUrl("https://graph.nhanh.vn/api/product/search");
		this.initialize();
	}
}
