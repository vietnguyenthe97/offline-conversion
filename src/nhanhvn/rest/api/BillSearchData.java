package nhanhvn.rest.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import nhanhvn.data.helper.MapToJsonConverter;

public class BillSearchData {
	
	public static void postRequest() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
	}
	
	public static void main(String[] args) {
		Map<String, String> data = new HashMap<>();
		data.put("cac", "15cm");
		data.put("lon", "10cm");
		String a = MapToJsonConverter.convertMapToJsonString(data);
		System.out.println(data);
	}
}
