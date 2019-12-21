package nhanhvn.data.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nhanhvn.data.helpers.DataHelper;
import nhanhvn.data.models.NhanhvnProduct;
import nhanhvn.data.models.NhanhvnProducts;
import nhanhvn.rest.api.ProductData;
import shared.persistence.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDataService extends  AbstractService {
    private ProductData productData;
    private NhanhvnProducts products;

    public ProductDataService() {
        productData = new ProductData();
        products = new NhanhvnProducts();
    }

    public NhanhvnProducts getProducts() {
        return products;
    }

    public void setProducts(NhanhvnProducts products) {
        this.products = products;
    }

    public void getProducts(String pageIndex) throws IOException {
        dataMap.put(PAGE, pageIndex);
        String data = DataHelper.convertMapToJsonString(dataMap);

        Gson gson = new Gson();
        JsonObject jsonData = productData.dataPostRequest(data);
        JsonObject productJson = jsonData.get("data").getAsJsonObject().get("products").getAsJsonObject();
        if(productJson != null) {
            List<NhanhvnProduct> products = new ArrayList<NhanhvnProduct>();
            //Map<String, Object> productDetail = DataHelper.convertJsonStringToMapObject(jsonProductData);
            for (Map.Entry<String, JsonElement> entry : productJson.entrySet()) {
                NhanhvnProduct productElement = gson.fromJson(entry.getValue(), NhanhvnProduct.class);
                products.add(productElement);
            }

            if(this.products.getProductList().isEmpty()) {
               this.products.setProductList(products);
            } else {
                products.stream().forEach(productElement -> {
                   this.products.getProductList().add(productElement);
                });
            }
            System.out.println(">>>>>>>>>> Total products of page " + pageIndex + ": " + products.size());
        }
    }

    public void getAndPersistAllProducts() throws IOException, SQLException {
        String data = DataHelper.convertMapToJsonString(dataMap);
        this.productData.dataPostRequest(data);
        int totalPages = this.productData.getTotalPages();
        for(int i=0; i<totalPages; i++) {
            System.out.println(">>>>>>>>>>>>>> Retrieving data from page " + (i+1) + " ...");
            getProducts("" + (i+1));
            System.out.println(">>>>>>>>>>>>>> Finished retrieving data from page " + (i+1));

            List<NhanhvnProduct> products = new ArrayList<>(this.products.getProductList());
            DatabaseConnection storingData = new DatabaseConnection();
            storingData.persistNhanhvnProducts(products);
            products.clear();
        }
        System.out.println("Total products: " + this.products.getProductList().size());
    }

    public static void main(String[] args) throws IOException {
        ProductDataService service = new ProductDataService(               );
        //service.getAllProducts();
        service.getProducts("1");
    }
}
