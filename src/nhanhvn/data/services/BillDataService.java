package nhanhvn.data.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nhanhvn.data.helpers.DataHelper;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import nhanhvn.data.models.NhanhvnBills;
import nhanhvn.rest.api.BillData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BillDataService extends AbstractService {
    private BillData billData;
    private NhanhvnBills nhanhvnBills;

    BillDataService() {
        billData = new BillData();
        nhanhvnBills = new NhanhvnBills();
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

        Gson gson = new Gson();
        JsonObject jsonData = billData.dataPostRequest(data);
        System.out.println(jsonData);
        JsonObject billJson = jsonData.get("data").getAsJsonObject().get("bill").getAsJsonObject();
        System.out.println(billJson);
        if(billJson != null) {
            for (Map.Entry<String, JsonElement> entry : billJson.entrySet()) {
                NhanhvnBill billElement = gson.fromJson(entry.getValue(), NhanhvnBill.class);
                JsonObject productJson = billJson.get(entry.getKey()).getAsJsonObject().get("products").getAsJsonObject();
                for(Map.Entry<String, JsonElement> productEntry : productJson.entrySet()) {
                    NhanhvnBillProductDetail productDetailElement =
                            gson.fromJson(productEntry.getValue(), NhanhvnBillProductDetail.class);
                    billElement.setProducts(productDetailElement);
                }
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

    public static void main(String[] args) throws IOException {
        BillDataService billDataService = new BillDataService();
        billDataService.getBills("1");
        billDataService.getNhanhvnBills().getNhanhvnBillList().forEach(bill -> {
            System.out.println(bill.getId() + ", " + bill.getCustomerName() + ", " + bill.getCustomerMobile() + ", " + bill.getProducts().getName() + ", " + bill.getProducts().getQuantity());
        });
    }

}
