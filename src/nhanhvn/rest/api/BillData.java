package nhanhvn.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nhanhvn.data.helper.DataHelper;
public class BillData extends AbstractData{
	public BillData() {
		this.setUrl("https://graph.nhanh.vn/api/bill/search");
		this.initialize();
	}
	
	
	public static void main(String[] args) throws IOException {
		BillData billData = new BillData();
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("icpp", "100");
		dataMap.put("page", "1");
		String data = DataHelper.convertMapToJsonString(dataMap);
	}
}
