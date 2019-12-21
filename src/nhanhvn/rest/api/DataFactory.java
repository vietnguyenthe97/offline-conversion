package nhanhvn.rest.api;

public class DataFactory {
    public static AbstractData createDataWithType(String datatype) {
        if(datatype.equalsIgnoreCase("bill")) {
            return new BillData();
        }

        if(datatype.equalsIgnoreCase("product")) {
            return new ProductData();
        }

        System.out.print("Unknown type, return default value as product");
        return new ProductData();
    }
}
