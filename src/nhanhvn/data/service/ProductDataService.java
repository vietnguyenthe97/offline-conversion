package nhanhvn.data.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nhanhvn.data.helper.DataHelper;
import nhanhvn.data.model.Product;
import nhanhvn.data.model.Products;
import nhanhvn.rest.api.ProductData;

public class ProductDataService {
    private final String MAX_PRODUCT = "100";
    private final String ICPP = "icpp";
    private final String PAGE = "page";
    private ProductData productData;
    private Products products;
    Map<String, String> dataMap;

    public ProductDataService() {
        productData = new ProductData();
        products = new Products();
        dataMap = new HashMap<>();
        dataMap.put(ICPP, MAX_PRODUCT);
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public void getProducts(String pageIndex) throws IOException {
        dataMap.put(PAGE, pageIndex);
        String data = DataHelper.convertMapToJsonString(dataMap);

        Gson gson = new Gson();
        JsonObject jsonData = productData.dataPostRequest(data);
        JsonObject productJson = jsonData.get("data").getAsJsonObject().get("products").getAsJsonObject();
        if(productJson != null) {
            List<Product> products = new ArrayList<Product>();
            //Map<String, Object> productDetail = DataHelper.convertJsonStringToMapObject(jsonProductData);
            for (Map.Entry<String, JsonElement> entry : productJson.entrySet()) {
                Product productElement = gson.fromJson(entry.getValue(), Product.class);
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

    public void getAllProducts() throws IOException {
        String data = DataHelper.convertMapToJsonString(dataMap);
        this.productData.dataPostRequest(data);
        int totalPages = this.productData.getTotalPages();
        for(int i=0; i<totalPages; i++) {
            System.out.println(">>>>>>>>>>>>>> Retrieving data from page " + (i+1) + " ...");
            getProducts("" + (i+1));
            System.out.println(">>>>>>>>>>>>>> Finished retrieving data from page " + (i+1));
        }
        System.out.println("Total products: " + this.products.getProductList().size());
    }

    public static void main(String[] args) throws IOException {
        ProductDataService service = new ProductDataService(               );
        service.getAllProducts();
    }
}
