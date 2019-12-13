package nhanhvn.data.model;

import java.util.ArrayList;
import java.util.List;

public class Products {
    List<Product> productList = new ArrayList<>();

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}
