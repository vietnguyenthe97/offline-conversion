package nhanhvn.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import nhanhvn.data.helper.DataHelper;

public class ProductData extends AbstractData{
	public ProductData() {
		this.setUrl("https://graph.nhanh.vn/api/product/search");
		this.initialize();
	}
	public String dataProductSearchPostRequest(String data)
			throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(this.getUrl());			
		
		postParams.add(new BasicNameValuePair("data", data));
		
		String checksum = DataHelper.generateChecksum(this.getApiSecretKey(), data);
		postParams.add(new BasicNameValuePair("checksum", checksum));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, Consts.UTF_8);
		httpPost.setEntity(entity);

		ResponseHandler<String> responseHandler = CustomResponseHandler.createResponseHandler();

		return httpClient.execute(httpPost, responseHandler);
	}
	
	public static void main(String[] args) throws IOException {
		ProductData productData = new ProductData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("icpp", 20);
		dataMap.put("page", 1);
		String data = DataHelper.convertMapToJsonString(dataMap);
		String response = productData.dataProductSearchPostRequest(data);
		JSONObject json = new JSONObject(response);
		JSONObject jsonData = (JSONObject) json.get("data");
		int jsonDataPage = (int) jsonData.get("totalPages");
		System.out.println(jsonDataPage);
	}
}
