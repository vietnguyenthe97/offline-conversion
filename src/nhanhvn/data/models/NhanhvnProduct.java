package nhanhvn.data.models;

import com.google.gson.annotations.Expose;
import com.opencsv.bean.CsvBindByPosition;

public class NhanhvnProduct {
	@Expose
    private String idNhanh;
	
	@Expose
    private String parentId;
	
	@Expose
    private String name;

	@Expose(serialize = true, deserialize = false)
    @CsvBindByPosition(position = 3)
    private transient String facebookId;

    public String getIdNhanh() {
        return idNhanh;
    }

    public void setIdNhanh(String idNhanh) {
        this.idNhanh = idNhanh;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
