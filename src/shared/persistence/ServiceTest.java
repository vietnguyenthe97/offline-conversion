package shared.persistence;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ServiceTest {
	public static void main(String[] args) throws IOException, SQLException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ProductDataService productDataService = new ProductDataService();
		GomhangProductService gomhangProductService = new GomhangProductService();
		BillDataService billDataService = new BillDataService();
		productDataService.getAndPersistAllProducts();
		stopWatch.stop();
		System.out.println("*********TIME FOR RUNNING NHANHVN PRODUCT SERVICE: " + stopWatch.getTime(TimeUnit.MINUTES) + " MINUTES");
		stopWatch.reset();
		stopWatch.start();
		gomhangProductService.getAndPersistProductsFromCsvFile();
		stopWatch.stop();
		System.out.println("*********TIME FOR RUNNING GOMHANG PRODUCT SERVICE: " + stopWatch.getTime(TimeUnit.MINUTES) + " MINUTES");
		stopWatch.reset();
		stopWatch.start();
		billDataService.getAndPersistAllBills();
		stopWatch.stop();
		System.out.println("*********TIME FOR RUNNING BILL SERVICE: " + stopWatch.getTime(TimeUnit.MINUTES) + " MINUTES");



//		BillData billData = new BillData();
//		String a = "{\"icpp\":\"100\",\"fromDate\":\"2019-10-20\",\"toDate\":\"2019-12-21\",\"type\":\"2\",\"modes\": [\"1\", \"2\", \"5\", \"6\", \"8\", \"10\"],\"page\":\"1\"}";
//		String check = DataHelper.generateChecksum(billData.getApiSecretKey(), a);
//		System.out.println(check);
	}
}
