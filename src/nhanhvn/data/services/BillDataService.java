package nhanhvn.data.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nhanhvn.data.helpers.DataHelper;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import nhanhvn.data.models.NhanhvnBills;
import nhanhvn.rest.api.BillData;
import shared.persistence.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BillDataService extends AbstractService {
	private final String FROM_DATE = "fromDate";
	private final String TO_DATE = "toDate";
	private final String TYPE = "type";
	private final String MODE = "mode";
    private BillData billData;
    private NhanhvnBills nhanhvnBills;

    public BillDataService() {
        billData = new BillData();
        nhanhvnBills = new NhanhvnBills();
        dataMap.put(TYPE, "2");
        dataMap.put(MODE, "1");
        dataMap.put(MODE, "2");
    }

    public BillData getBillData() {
        return billData;
    }

    public void setBillData(BillData billData) {
        this.billData = billData;
    }

    public NhanhvnBills getNhanhvnBills() {
        return nhanhvnBills;
    }

    public void setNhanhvnBills(NhanhvnBills nhanhvnBills) {
        this.nhanhvnBills = nhanhvnBills;
    }

    public void getBills(String pageIndex) throws IOException {
        dataMap.put(PAGE, pageIndex);
        List<NhanhvnBill> bills = new ArrayList<>();
        List<NhanhvnBillProductDetail> billDetails = new ArrayList<>();

        String data = DataHelper.convertMapToJsonString(dataMap);

        Gson billGson = new GsonBuilder()
        		.excludeFieldsWithoutExposeAnnotation()
                .create();
        
        JsonObject jsonData = billData.dataPostRequest(data);
        System.out.println(jsonData);
        JsonObject billJson = jsonData.get("data").getAsJsonObject().get("bill").getAsJsonObject();
        System.out.println(billJson);
        if(billJson != null) {
            for (Map.Entry<String, JsonElement> entry : billJson.entrySet()) {
                NhanhvnBill billElement = billGson.fromJson(entry.getValue(), NhanhvnBill.class);
                JsonObject productJson = billJson.get(entry.getKey()).getAsJsonObject().get("products").getAsJsonObject();
                for(Map.Entry<String, JsonElement> productEntry : productJson.entrySet()) {
                    System.out.println(productJson.entrySet());
                    Gson productGson = new GsonBuilder().create();
                    NhanhvnBillProductDetail productDetailElement =
                            productGson.fromJson(productEntry.getValue(), NhanhvnBillProductDetail.class);
                    System.out.println(productDetailElement.getQuantity());
                    billDetails.add(productDetailElement);
                }
                billElement.setProducts(billDetails);
                bills.add(billElement);
            }

            if(this.nhanhvnBills.getNhanhvnBillList().isEmpty()) {
                this.nhanhvnBills.setNhanhvnBillList(bills);
            } else {
                bills.stream().forEach(billElement -> {
                    this.nhanhvnBills.getNhanhvnBillList().add(billElement);
                });
            }
            System.out.println(">>>>>>>>>> Total products of page " + pageIndex + ": " + bills.size());
        }
    }

    public void getAndPersistAllBills() throws IOException, SQLException {
    	LocalDate currentDate = LocalDate.now();
    	LocalDate dateFromPrevious62Days = currentDate.minusDays(62);
    	dataMap.put(FROM_DATE, dateFromPrevious62Days.toString());
    	dataMap.put(TO_DATE, currentDate.toString());
        String data = DataHelper.convertMapToJsonString(dataMap);
        this.billData.dataPostRequest(data);
        
        int totalPages = this.billData.getTotalPages();
        System.out.println("Total pages of bills: " + totalPages);
        for(int i=0; i<totalPages; i++) {
            System.out.println(">>>>>>>>>>>>>> Retrieving data from page " + (i+1) + " ...");
            getBills("" + (i+1));

            List<NhanhvnBill> bills =  this.nhanhvnBills.getNhanhvnBillList();
            System.out.println(">>>>>>>>>>>>>> Finished retrieving data from page " + (i+1));
            DatabaseConnection storingData = new DatabaseConnection();
            storingData.persistNhanhvnBills(bills);
            bills.clear();
        }
    }

    public static void main(String[] args) throws IOException {
        BillDataService billDataService = new BillDataService();
    }

}
