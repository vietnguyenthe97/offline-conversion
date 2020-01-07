package nhanhvn.data.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import nhanhvn.data.models.NhanhvnBills;
import nhanhvn.rest.api.BillData;
import shared.datahelper.DataHelper;
import shared.persistence.DatabaseConnection;

public class BillDataService extends AbstractService {
	private final String FROM_DATE = "fromDate";
	private final String TO_DATE = "toDate";
	private final String TYPE = "type";
	private final String MODES = "modes";
	private final String MODE = "mode";
    private BillData billData;
    private NhanhvnBills nhanhvnBills;

    public BillDataService() {
        billData = new BillData();
        nhanhvnBills = new NhanhvnBills();
        customizeRequest();
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

    public void customizeRequest() {
        LocalDate currentDate = LocalDate.now();
        LocalDate dateFromPrevious62Days = currentDate.minusDays(62);
        dataMap.put(FROM_DATE, dateFromPrevious62Days.toString());
        dataMap.put(TO_DATE, currentDate.toString());
        dataMap.put(TYPE, 2);
        //List<Integer> modeList = Arrays.asList(1, 2, 5, 6, 8, 10);
        dataMap.put(MODE, 2);
    }

    public void getBills(String pageIndex) throws IOException {
        dataMap.put(PAGE, pageIndex);
        List<NhanhvnBill> bills = new ArrayList<>();
        List<NhanhvnBillProductDetail> billDetails = null;

        String data = DataHelper.convertMapToJsonString(dataMap);
        System.out.println(data);
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
                    billDetails = new ArrayList<>();
                    System.out.println(productJson.entrySet());
                    Gson productGson = new GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                    NhanhvnBillProductDetail productDetailElement =
                            productGson.fromJson(productEntry.getValue(), NhanhvnBillProductDetail.class);
                    System.out.println(productDetailElement.getQuantity());
                    billDetails.add(productDetailElement);
                }

                if (billDetails != null) {
                    System.out.println(">>>>>>>>>> Total products of bill: " + billDetails.size());
                    billElement.setProducts(billDetails);
                    billDetails.stream().forEach(e -> System.out.println(e.getBillId()));
                }
                bills.add(billElement);
            }

            if (this.nhanhvnBills.getNhanhvnBillList().isEmpty()) {
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
        String data = DataHelper.convertMapToJsonString(dataMap);
        System.out.println(data);
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
        System.out.println("Total bills: " + this.nhanhvnBills.getNhanhvnBillList().size());
    }
    
    public static void main(String[] args) throws JsonProcessingException {
    	BillDataService service = new BillDataService();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Integer> modeList = Arrays.asList(1, 2, 5, 6, 8, 10);
		Gson gson = new Gson();
		String jsonString = gson.toJson(modeList);
		String data = "{\"icpp\":\"100\",\"fromDate\":\"2019-11-02\",\"toDate\":\"2020-01-03\",\"type\":\"2\", \"modes\": [1, 2, 5, 6, 8, 10]}";
				 
		//DataHelper.convertMapToJsonString(service.dataMap);
		String checksum = DataHelper.generateChecksum("Ne658esvsdf_2tdfytregfd_ty8t76ry", data);
		System.out.println(data);
		System.out.println(checksum);
		map.put("something", modeList);
		map.put("Conscience", "Stricken");
		System.out.println(map);
		String jsonStringS = gson.toJson(map);
		System.out.println(jsonStringS);
		
		System.out.println(DataHelper.convertMapToJsonString(service.dataMap));
		
	}
}
