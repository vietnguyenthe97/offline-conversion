package gomhangvn.data.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import gomhangvn.data.model.GomhangProduct;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import gomhangvn.data.model.GomhangProducts;

public class GomhangProductService {
	private GomhangProducts gomhangProducts = new GomhangProducts();
	private final String fileName = "gomhangProduct.csv";

	public GomhangProducts getGomhangProducts() {
		return gomhangProducts;
	}

	public int downloadGomhangProductService() {
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
		
		if(entity != null) {
			try {
				InputStream inputStream = entity.getContent();
				FileOutputStream fileOutputStream = new FileOutputStream(fileName);
				int readByte;
				while((readByte = inputStream.read()) != -1) {
					fileOutputStream.write(readByte);
				}
				fileOutputStream.close();
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseCode;
	}
	
	public void getProductsFromCsvFile() {
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

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
        System.out.println("Total products added: " + gomhangProducts.getGomhangProductList().size());
	}
	
	public static void main(String[] args) {
		GomhangProductService a = new GomhangProductService();
		a.downloadGomhangProductService();
		a.getProductsFromCsvFile();
		for(int i=0; i< a.getGomhangProducts().getGomhangProductList().size(); i++) {
			System.out.println(a.getGomhangProducts().getGomhangProductList().get(i).getId());
			System.out.println(a.getGomhangProducts().getGomhangProductList().get(i).getName());
		}

	}
}
