package nhanhvn.data.models;

import java.util.ArrayList;
import java.util.List;

public class NhanhvnProducts {
    List<NhanhvnProduct> productList = new ArrayList<>();

    public List<NhanhvnProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<NhanhvnProduct> productList) {
        this.productList = productList;
    }
}
