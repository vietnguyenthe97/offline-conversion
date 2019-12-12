package nhanhvn.rest.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import nhanhvn.data.helper.DataHelper;
import nhanhvn.security.apistorage.ApiCredentials;
import nhanhvn.security.apistorage.ApiHelper;
import org.json.JSONObject;

public abstract class AbstractData {
	 protected ApiCredentials apiCredentials;
	 protected String url;
	 protected List<NameValuePair> postParams;
	 protected int totalPages;

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public void initialize() {
	 apiCredentials = ApiHelper.retrieveApiCredentials();
	 postParams = new ArrayList<NameValuePair>();
	 postParams.add(new BasicNameValuePair("version", apiCredentials.getVersion()));
	 postParams.add(new BasicNameValuePair("apiUsername", apiCredentials.getApiUserName()));
	 }

	 public String getApiVersion() {
	 return apiCredentials.getVersion();
	 }

	 public String getApiUserName() {
	 return apiCredentials.getApiUserName();
	 }

	 public String getApiSecretKey() {
	 return apiCredentials.getApiSecretKey();
	 }

	 public void setUrl(String url) {
	 this.url = url;
	 }

	 public String getUrl() {
	 return url;
	 }

	 public void setPostParams(List<NameValuePair> postParams) {
	 this.postParams = postParams;
	 }

	 public List<NameValuePair> getPostParams() {
	 return postParams;
	 }

	 /**
	 * Add param for post request
	 * @param name fieldname as string
	 * @param value value of the fieldname as string
	 */
	public void addParam(String name, String value) {
		Objects.nonNull(postParams);
		postParams.add(new BasicNameValuePair(name, value));
	}
	
	/**
	 * Sending post request
	 * @param data data to send
	 * @return http response as string
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JsonObject dataPostRequest(String data)
			throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(this.getUrl());

		this.addParam("data", data);

		String checksum = DataHelper.generateChecksum(this.getApiSecretKey(), data);
		this.addParam("checksum", checksum);

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, Consts.UTF_8);
		httpPost.setEntity(entity);

		ResponseHandler<String> responseHandler = CustomResponseHandler.createResponseHandler();

		String response = httpClient.execute(httpPost, responseHandler);
		JsonObject responseInJson = new JsonObject();
		if(response != null) {
			responseInJson = new JsonParser().parse(response).getAsJsonObject();
			this.totalPages = responseInJson.get("data").getAsJsonObject().get("totalPages").getAsInt();
		}

		return responseInJson;
	}
}
