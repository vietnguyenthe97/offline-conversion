package nhanhvn.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import nhanhvn.data.helper.DataHelper;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class BillSearchData {
	private static final String secretKey = "Ne658esvsdf_2tdfytregfd_ty8t76ry";
	public static String postRequest(String version, String apiUsername, String data)
			throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://graph.nhanh.vn/api/product/search");

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("version", version));
		postParams.add(new BasicNameValuePair("apiUsername", apiUsername));
		postParams.add(new BasicNameValuePair("data", data));
		String checksum = DataHelper.generateChecksum(secretKey, data);
		postParams.add(new BasicNameValuePair("checksum", checksum));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, Consts.UTF_8);
		httpPost.setEntity(entity);

		ResponseHandler<String> responseHandler = CustomResponseHandler.createResponseHandler();

		String responseBody = httpClient.execute(httpPost, responseHandler);
		return responseBody;
	}
	
	public static void main(String[] args) throws IOException {
		String version = "1.0";
		String apiUsername = "Gomhang.vn";
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("icpp", 20);
		dataMap.put("page", 1);
		String data = DataHelper.convertMapToJsonString(dataMap);
		String response = postRequest(version, apiUsername, data);
		System.out.println(DataHelper.convertStringToJson(response));
	}
}
