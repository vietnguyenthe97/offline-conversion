package nhanhvn.data.models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class NhanhvnExportProduct {
    @CsvBindByName(column = "idNhanh")
    @CsvBindByPosition(position = 0)
    private String idNhanh;

    @CsvBindByName(column = "parentId")
    @CsvBindByPosition(position = 1)
    private String parentId;

    @CsvBindByName(column = "productName")
    @CsvBindByPosition(position = 2)
    private String name;

    @CsvBindByName(column = "facebookId")
    @CsvBindByPosition(position = 3)
    private String facebookId;

    //number of products sold in the bills, all the sold child products are counted for their parent products
    @CsvBindByName(column = "quantity")
    @CsvBindByPosition(position = 4)
    private float totalProductsSold;

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

    public float getTotalProductsSold() {
        return totalProductsSold;
    }

    public void setTotalProductsSold(float totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }
}
