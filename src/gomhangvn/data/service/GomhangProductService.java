package gomhangvn.data.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import gomhangvn.data.model.GomhangProducts;

public class GomhangProductService {
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
				String fileName = "gomhangProduct.csv";
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
	
	public GomhangProducts getProductsFromCsvFile(String csvFile) {
		GomhangProducts gomhangProducts = new GomhangProducts();
		String line = "";
		String splitBy = ",";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"UTF8"))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(splitBy);
                System.out.println("id = " + values[0] + " , name =" + values[1] + "]");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }		
		return gomhangProducts;
	}
	
	public static void main(String[] args) {
		GomhangProductService a = new GomhangProductService();
		a.downloadGomhangProductService();
		a.getProductsFromCsvFile("gomhangProduct.csv");
	}
}
