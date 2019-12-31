package offlineconversion.data.models;

public class ConversionData {
    Contents contents;
    String matchKey;
    String currency;
    double value;
    String eventName;
    String eventTime;
    String uploadTag;

    public String getUploadTag() {
        return uploadTag;
    }

    public void setUploadTag(String uploadTag) {
        this.uploadTag = uploadTag;
    }

    public ConversionData() {
        contents = new Contents();
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String match_keys) {
        this.matchKey = match_keys;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
