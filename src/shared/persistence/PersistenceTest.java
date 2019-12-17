package shared.persistence;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;

import java.io.IOException;
import java.sql.SQLException;

public class PersistenceTest {
    public static void main(String[] args) throws IOException, SQLException {
        GomhangProductService gomhangProductService = new GomhangProductService();
        ProductDataService productDataService = new ProductDataService();
        BillDataService billDataService = new BillDataService();
        productDataService.getAndPersistAllProducts();
        gomhangProductService.getAndPersistProductsFromCsvFile();
        billDataService.getAndPersistAllBills();
    }
}
