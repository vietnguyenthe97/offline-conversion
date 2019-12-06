package nhanhvn.rest.api;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import nhanhvn.data.helper.DataHelper;

public class BillSearchData {	
	public static void postRequest(String version, String storeId, String apiUsername, String data) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://graph.nhanh.vn/api/product/search");
	}
	
	public static void main(String[] args) {
		Map<String, String> data = new HashMap<>();
		data.put("cac", "15cm");
		data.put("lon", "10cm");
		String a = DataHelper.convertMapToJsonString(data);

	}
}
