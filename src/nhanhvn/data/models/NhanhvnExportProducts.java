package nhanhvn.data.models;

import java.util.ArrayList;
import java.util.List;

public class NhanhvnExportProducts {
    List<NhanhvnExportProduct> nhanhvnExportProductList = new ArrayList<>();

    public List<NhanhvnExportProduct> getNhanhvnExportProductList() {
        return nhanhvnExportProductList;
    }

    public void setNhanhvnExportProductList(List<NhanhvnExportProduct> nhanhvnExportProductList) {
        this.nhanhvnExportProductList = nhanhvnExportProductList;
    }
}
