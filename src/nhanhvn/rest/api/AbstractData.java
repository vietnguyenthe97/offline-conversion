package nhanhvn.rest.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nhanhvn.security.apistorage.ApiCredentials;
import nhanhvn.security.apistorage.ApiHelper;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import shared.datahelper.DataHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractData {
	protected ApiCredentials apiCredentials;
	protected String url;
	protected List<NameValuePair> postParams;
	protected int totalPages;
	protected CloseableHttpClient httpClient;
	protected int retryTimes = 0;
	public AbstractData() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(28800)
				.setConnectTimeout(28800)
				.setSocketTimeout(28800)
				.build();
		this.httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setMaxConnPerRoute(4)
				.setMaxConnTotal(4)
				.build();
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public void initialize() {
		apiCredentials = ApiHelper.getApiCredentials();
		postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("version", apiCredentials.getApiDetails().getVersion()));
		postParams.add(new BasicNameValuePair("apiUsername", apiCredentials.getApiDetails().getApiUserName()));
	}

	 public int getRetryTimes() { return retryTimes; }

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
		Objects.requireNonNull(postParams);
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
		HttpPost httpPost = new HttpPost(this.getUrl());
		this.addParam("data", data);

		String checksum = DataHelper.generateChecksum(apiCredentials.getApiDetails().getApiSecretKey(), data);
		this.addParam("checksum", checksum);

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, Consts.UTF_8);
		httpPost.setEntity(entity);

		ResponseHandler<String> responseHandler = CustomResponseHandler.createResponseHandler();
		String response = null;
		JsonObject responseInJson = new JsonObject();

		final int MAX_RETRY = 1000;
		while(true) {
			try {
				response = httpClient.execute(httpPost, responseHandler);
				if(response != null) {
					responseInJson = new JsonParser().parse(response).getAsJsonObject();
					this.totalPages = responseInJson.get("data").getAsJsonObject().get("totalPages").getAsInt();
					System.out.println(postParams);
				}
				break;
			} catch (IOException e) {
				if(e instanceof NoHttpResponseException) {
					retryTimes++;
					response = httpClient.execute(httpPost, responseHandler);
					if(retryTimes == MAX_RETRY) {
						throw e;
					}
				} else {
					throw e;
				}
			}
		}
		return responseInJson;
	}
}
