package nhanhvn.data.models;

import com.opencsv.bean.CsvBindByPosition;

public class IdConversionObject {
    @CsvBindByPosition(position = 0)
    private String idNhanh;

    @CsvBindByPosition(position = 1)
    private String parentId;

    @CsvBindByPosition(position = 3)
    private String facebookId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIdNhanh() {
        return idNhanh;
    }

    public void setIdNhanh(String idNhanh) {
        this.idNhanh = idNhanh;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
