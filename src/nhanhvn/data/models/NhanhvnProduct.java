package nhanhvn.data.models;

import com.google.gson.annotations.Expose;
import com.opencsv.bean.CsvBindByPosition;

public class NhanhvnProduct {
	@Expose
	@CsvBindByPosition(position = 0)
    private String idNhanh;
	
	@Expose
	@CsvBindByPosition(position = 1)
    private String parentId;
	
	@Expose
	@CsvBindByPosition(position = 2)
    private String name;

	@Expose(serialize = true, deserialize = false)
    @CsvBindByPosition(position = 3)
    private String facebookId;

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
