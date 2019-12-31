package nhanhvn.security.apistorage;

public class FacebookOfflineConversionApiCredentials {
    private String userAccessToken;
    private String version;
    private String offlineEventSetId;

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public String getVersion() {
        return version;
    }

    public String getOfflineEventSetId() {
        return offlineEventSetId;
    }

    public void setUserAccessToken(String userAccessToken) {
        this.userAccessToken = userAccessToken;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setOfflineEventSetId(String offlineEventSetId) {
        this.offlineEventSetId = offlineEventSetId;
    }
}