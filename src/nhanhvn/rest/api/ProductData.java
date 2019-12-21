package nhanhvn.rest.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import shared.datahelper.DataHelper;
import nhanhvn.data.models.NhanhvnProducts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductData extends AbstractData{
	public ProductData() {
		this.setUrl("https://graph.nhanh.vn/api/product/search");
		this.initialize();
	}
	public static void main(String[] args) throws IOException {
		ProductData productData = new ProductData();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("icpp", "100");
		dataMap.put("page", "238");
		String data = DataHelper.convertMapToJsonString(dataMap);

		JsonObject jsonNew = productData.dataPostRequest(data);

		JsonObject object = jsonNew.get("data").getAsJsonObject().get("products").getAsJsonObject();
		int pages = jsonNew.get("data").getAsJsonObject().get("totalPages").getAsInt();
		System.out.println("Total pages: " + pages);
		Gson gson = new Gson();
		//System.out.println(object);

		NhanhvnProducts p = new NhanhvnProducts();

		System.out.println(p.getProductList().size());
		p.getProductList().forEach(pro -> System.out.println(pro.getName()));
	}
}
