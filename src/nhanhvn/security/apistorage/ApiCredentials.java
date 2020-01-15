package nhanhvn.security.apistorage;

public class ApiCredentials {
	private NhanhvnApiCredentials apiDetails;
	private FacebookOfflineConversionApiCredentials facebookDetails;
	private DatabaseCredentials databaseDetails;

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

	public DatabaseCredentials getDatabaseDetails() {
		return databaseDetails;
	}

	public void setDatabaseDetails(DatabaseCredentials databaseDetails) {
		this.databaseDetails = databaseDetails;
	}
}
