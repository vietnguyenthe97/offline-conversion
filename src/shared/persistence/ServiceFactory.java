package shared.persistence;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.AbstractService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;

public class ServiceFactory {
    public static AbstractService createNhanhvnService(String datatype) {
        if(datatype.equalsIgnoreCase("bill")) {
            return new BillDataService();
        }

        if(datatype.equalsIgnoreCase("product")) {
            return new ProductDataService();
        }

        System.out.print("Unknown type, return null");
        return null;
    }

    public static GomhangProductService createGomhangService(String datatype) {
        if(datatype.equalsIgnoreCase("product")) {
            return new GomhangProductService();
        }

        System.out.print("Unknown type, return null");
        return null;
    }
}
