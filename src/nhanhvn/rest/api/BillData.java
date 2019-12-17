package nhanhvn.rest.api;

import com.google.gson.JsonObject;
import nhanhvn.data.helpers.DataHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;

public class BillData extends AbstractData{
	public BillData() {
		this.setUrl("https://graph.nhanh.vn/api/bill/search");
		this.initialize();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(28800)
				.setConnectTimeout(28800)
				.setSocketTimeout(28800)
				.build();
		this.httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				//in case of NoHttpResponseException, retry sending x times
				.setRetryHandler(new DefaultHttpRequestRetryHandler(10, false))
				.build();
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
