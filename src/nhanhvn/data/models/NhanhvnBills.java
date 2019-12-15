package nhanhvn.data.models;

import java.util.ArrayList;
import java.util.List;

public class NhanhvnBills {
    List<NhanhvnBill> nhanhvnBillList = new ArrayList<>();

    public List<NhanhvnBill> getNhanhvnBillList() {
        return nhanhvnBillList;
    }

    public void setNhanhvnBillList(List<NhanhvnBill> nhanhvnBillList) {
        this.nhanhvnBillList = nhanhvnBillList;
    }
}
