package nhanhvn.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nhanhvn.data.model.Product;
import nhanhvn.data.model.Products;
import org.json.JSONArray;
import org.json.JSONObject;

import nhanhvn.data.helper.DataHelper;

public class ProductData extends AbstractData{
	public ProductData() {
		this.setUrl("https://graph.nhanh.vn/api/product/search");
		this.initialize();
	}

	public static void main(String[] args) throws IOException {
		ProductData productData = new ProductData();
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("icpp", "100");
		dataMap.put("page", "238");
		String data = DataHelper.convertMapToJsonString(dataMap);

		JsonObject jsonNew = productData.dataPostRequest(data);

		JsonObject object = jsonNew.get("data").getAsJsonObject().get("products").getAsJsonObject();
		int pages = jsonNew.get("data").getAsJsonObject().get("totalPages").getAsInt();
		System.out.println("Total pages: " + pages);
		Gson gson = new Gson();
		//System.out.println(object);

		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			Product product = gson.fromJson(entry.getValue(), Product.class);
			System.out.println(product.getIdNhanh());
		}


		//System.out.println(dataFromJson);


	}
}
