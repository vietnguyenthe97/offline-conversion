package nhanhvn.data.services;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import nhanhvn.data.models.IdConversionObject;
import nhanhvn.data.models.NhanhvnExportProduct;
import nhanhvn.data.models.NhanhvnExportProducts;
import shared.persistence.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Iterator;

public class TransactionService extends AbstractService {
	private final String PRODUCT_EXPORT_PATH = "resources/exported_product_list.csv";
	private final String PRODUCT_IMPORT_PATH = "resources/facebookid_mapping_sheet.csv";
	private DatabaseConnection databaseConnection = new DatabaseConnection();
	private boolean getFromAllBills;

	public boolean isGetFromAllBills() {
		return getFromAllBills;
	}

	public void setGetFromAllBills(boolean getFromAllBills) {
		this.getFromAllBills = getFromAllBills;
	}

	public String getAbsoluteImportPath() {
		File file = new File(PRODUCT_IMPORT_PATH);
		return file.getAbsolutePath();
	}


	public void exportNhanhvnProducts() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, SQLException {
		NhanhvnExportProducts nhanhvnExportProducts = databaseConnection.getNhanhvnParentProductsFromDb(this.getFromAllBills);
		
		File file = new File(PRODUCT_EXPORT_PATH);
		if (!file.exists()) {
			file.createNewFile();
		} 
		
		Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        StatefulBeanToCsv<NhanhvnExportProduct> beanToCsv = new StatefulBeanToCsvBuilder<NhanhvnExportProduct>(writer)
                .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .build();
        
		String headerRecord = "\"idNhanh\",\"parentId\",\"productName\",\"facebookId\", \"quantity\"\n";
		writer.write(headerRecord);
		beanToCsv.write((nhanhvnExportProducts.getNhanhvnExportProductList()));
		System.out.println("File duoc export o duong dan: " + file.getAbsolutePath());
		writer.close();
	}
	
    public void updateFacebookId() throws SQLException, IOException {
		File file = new File(PRODUCT_IMPORT_PATH);
		if (!file.exists()) {
			System.out.println("Khong tim thay file o :" + file.getAbsolutePath());
		} else {
			Reader reader;
			reader = Files.newBufferedReader(Paths.get(file.getPath()));

			if(reader != null) {
				System.out.println("Reading file: " + file.getAbsolutePath());
				CsvToBean<IdConversionObject> csvToBean = new CsvToBeanBuilder<IdConversionObject>(reader)
						.withType(IdConversionObject.class)
						.withIgnoreLeadingWhiteSpace(true)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withSkipLines(1)
						.build();
				Iterator<IdConversionObject> csvUserIterator = csvToBean.iterator();
				while (csvUserIterator.hasNext()) {
					IdConversionObject idConversionObject = csvUserIterator.next();
					databaseConnection.persistFacebookId(idConversionObject);
				}
				databaseConnection.updateFacebookIdFromProductTableToBillDetails();
			}
		}
    }
}
