package nhanhvn.security.apistorage;

public class ApiCredentials {
	private FacebookOfflineConversionApiCredentials facebookDetails;
	private NhanhvnApiCredentials apiDetails;

	public FacebookOfflineConversionApiCredentials getFacebookDetails() {
		return facebookDetails;
	}

	public void setFacebookDetails(FacebookOfflineConversionApiCredentials facebookDetails) {
		this.facebookDetails = facebookDetails;
	}

	public NhanhvnApiCredentials getApiDetails() {
		return apiDetails;
	}

	public void setApiDetails(NhanhvnApiCredentials apiDetails) {
		this.apiDetails = apiDetails;
	}
}
