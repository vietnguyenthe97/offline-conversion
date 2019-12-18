package shared.persistence;

import java.io.IOException;
import java.sql.SQLException;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;

public class ServiceTest {
	public static void main(String[] args) throws IOException, SQLException {
		ProductDataService productDataService = new ProductDataService();
		GomhangProductService gomhangProductService = new GomhangProductService();
		BillDataService billDataService = new BillDataService();
		//productDataService.getAndPersistAllProducts();
		//gomhangProductService.getAndPersistProductsFromCsvFile();
		billDataService.getAndPersistAllBills();
	}
}
