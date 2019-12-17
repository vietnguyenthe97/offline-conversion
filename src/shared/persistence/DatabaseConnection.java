package shared.persistence;

import gomhangvn.data.model.GomhangProduct;
import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBills;
import nhanhvn.data.models.NhanhvnProduct;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;
import nhanhvn.rest.api.BillData;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection = null;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection makeDbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nhanhvnstorage", "root", "langthanG*1992");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void persistNhanhvnProducts(ProductDataService productDataService) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_product_list (idNhanh, productName, parentId)" +
                    "VALUES(?,?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            productDataService.getAllProducts();
            List<NhanhvnProduct> products = new ArrayList<>();
            products = productDataService.getProducts().getProductList();
            int totalChanges=0;
            for(NhanhvnProduct productElement: products) {
                String idNhanh = productElement.getIdNhanh();
                String name = productElement.getName();
                String parentId = productElement.getParentId();
                preparedStatement.setString(1, idNhanh);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, parentId);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting nhanhvn products ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistGomhangvnProducts(GomhangProductService gomhangProductService) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO gomhangvn_product_list (id, productName)" +
                    "VALUES(?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            gomhangProductService.downloadGomhangProductService();
            gomhangProductService.getProductsFromCsvFile();

            List<GomhangProduct> products = gomhangProductService.getGomhangProducts().getGomhangProductList();
            int totalChanges=0;
            for(GomhangProduct productElement: products) {
                String id = productElement.getId();
                String name = productElement.getName();
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting gomhang products ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistNhanhvnBills(BillDataService billDataService) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_bills (id, customerName, customerMobile, createdDateTime, money)" +
                    "VALUES(?,?,?,?,?)" +
                    "ON DUPLICATE KEY UPDATE" + 
                    " customerName = VALUES(customerName)," + 
                    " customerMobile = VALUES(customerMobile)," +
            		" createdDateTime = VALUES(createdDateTime)," +
            		" money = VALUES(money);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            billDataService.getBills("1");
            List<NhanhvnBill> bills = billDataService.getNhanhvnBills().getNhanhvnBillList();
            int totalChanges=0;
            for(NhanhvnBill billElement: bills) {
                String id = billElement.getId();
                String customerName = billElement.getCustomerName();
                String customerMobile = billElement.getCustomerMobile();
                String createdDateTime = billElement.getCreatedDateTime();
                double money = billElement.getMoney();
                
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, customerName);
                preparedStatement.setString(3, customerMobile);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(createdDateTime));
                preparedStatement.setDouble(5, money);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting nhanhvn bills ==================");
            System.out.println("================== Total Changes: " + totalChanges + " ==================");
            connection.close();
        }
    }
    
    public static void main(String[] args) throws SQLException, IOException {
        DatabaseConnection dbConn = new DatabaseConnection();
//        ProductDataService productDataService = new ProductDataService();
//        dbConn.persistNhanhvnProducts(productDataService);
//
//        GomhangProductService gomhangProductService = new GomhangProductService();
//        dbConn.persistGomhangvnProducts(gomhangProductService);
        
        BillDataService billDataService = new BillDataService();
        dbConn.persistNhanhvnBills(billDataService);
    }
}
