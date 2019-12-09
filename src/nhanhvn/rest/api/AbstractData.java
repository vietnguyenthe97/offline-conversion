package nhanhvn.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import nhanhvn.security.apistorage.ApiCredentials;
import nhanhvn.security.apistorage.ApiHelper;

public abstract class AbstractData {
	protected ApiCredentials apiCredentials;
	protected String url;
	protected List<NameValuePair> postParams;
	
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
}
