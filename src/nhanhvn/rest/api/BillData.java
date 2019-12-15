package nhanhvn.rest.api;

import com.google.gson.JsonObject;
import nhanhvn.data.helpers.DataHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BillData extends AbstractData{
	public BillData() {
		this.setUrl("https://graph.nhanh.vn/api/bill/search");
		this.initialize();
	}

	public static void main(String[] args) throws IOException {
		BillData b = new BillData();
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("icpp", "100");
		dataMap.put("page", "238");
		String data = DataHelper.convertMapToJsonString(dataMap);

		JsonObject jsonNew = b.dataPostRequest(data);


		int pages = jsonNew.get("data").getAsJsonObject().get("totalPages").getAsInt();
		System.out.println("Total pages: " + pages);
		System.out.println(jsonNew);
	}
}
