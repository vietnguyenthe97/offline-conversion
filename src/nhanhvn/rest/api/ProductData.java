package nhanhvn.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import nhanhvn.data.helper.DataHelper;

public class ProductData extends AbstractData{
	public ProductData() {
		this.setUrl("https://graph.nhanh.vn/api/product/search");
		this.initialize();
	}
	
	public static void main(String[] args) throws IOException {
		ProductData productData = new ProductData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("icpp", 20);
		dataMap.put("page", 1);
		String data = DataHelper.convertMapToJsonString(dataMap);
		String response = productData.dataPostRequest(data);
		JSONObject json = new JSONObject(response);
		JSONObject jsonData = (JSONObject) json.get("data");
		int jsonDataPage = (int) jsonData.get("totalPages");
		System.out.println(jsonDataPage);
	}
}
