package gomhangvn.data.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import gomhangvn.data.models.GomhangProduct;
import gomhangvn.data.models.GomhangProducts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import shared.persistence.DatabaseConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GomhangProductService {
	private GomhangProducts gomhangProducts = new GomhangProducts();
	private final String fileName = "gomhangProduct.csv";

	public GomhangProducts getGomhangProducts() {
		return gomhangProducts;
	}

	public void downloadGomhangProductService() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://gomhang.vn/productid.php");
		HttpResponse response;
		HttpEntity entity = null;
		int responseCode = 0;
		try {
			response = httpClient.execute(request);
			entity = response.getEntity();
			responseCode = response.getStatusLine().getStatusCode();
			System.out.println("Download status: " + responseCode);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (entity != null) {
			try {
				InputStream inputStream = entity.getContent();
				FileOutputStream fileOutputStream = new FileOutputStream(fileName);
				int readByte;
				while ((readByte = inputStream.read()) != -1) {
					fileOutputStream.write(readByte);
				}
				fileOutputStream.close();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getAndPersistProductsFromCsvFile() throws SQLException {
		downloadGomhangProductService();
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (reader != null) {
			CsvToBean<GomhangProduct> csvToBean = new CsvToBeanBuilder<GomhangProduct>(reader)
					.withType(GomhangProduct.class)
					.withIgnoreLeadingWhiteSpace(true)
					.withIgnoreQuotations(true)
					.withSkipLines(1)
					.build();

			Iterator<GomhangProduct> csvUserIterator = csvToBean.iterator();
			while (csvUserIterator.hasNext()) {
				GomhangProduct gomhangProduct = csvUserIterator.next();
				gomhangProducts.getGomhangProductList().add(gomhangProduct);
			}

			List<GomhangProduct> products = new ArrayList<GomhangProduct>(this.gomhangProducts.getGomhangProductList());
			DatabaseConnection storingData = new DatabaseConnection();
			storingData.persistGomhangvnProducts(products);
			products.clear();
			System.out.println("Total products added: " + gomhangProducts.getGomhangProductList().size());
		}
	}
}

