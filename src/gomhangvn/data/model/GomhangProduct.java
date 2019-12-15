package gomhangvn.data.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class GomhangProduct {
	@CsvBindByPosition(position = 0)
	String id;

	@CsvBindByPosition(position = 1)
	String name;

	public GomhangProduct() {}

	public GomhangProduct(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
