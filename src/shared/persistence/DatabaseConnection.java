package shared.persistence;

import gomhangvn.data.model.GomhangProduct;
import nhanhvn.data.models.NhanhvnBill;
import nhanhvn.data.models.NhanhvnBillProductDetail;
import nhanhvn.data.models.NhanhvnProduct;

import java.io.IOException;
import java.sql.*;
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

    public void persistNhanhvnProducts(List<NhanhvnProduct> products) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_product_list (idNhanh, productName, parentId)" +
                    "VALUES(?,?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

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
            System.out.println("================== Finished persisting nhanhvnproducts: " + products.size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistGomhangvnProducts(List<GomhangProduct> products) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO gomhangvn_product_list (id, productName)" +
                    "VALUES(?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int totalChanges=0;
            for(GomhangProduct productElement: products) {
                String id = productElement.getId();
                String name = productElement.getName();
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting gomhangvnproducts: " + products.size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public void persistNhanhvnBills(List<NhanhvnBill> bills) throws SQLException, IOException {
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

            int totalProducts=0;
            int status=0;
            for(NhanhvnBill billElement: bills) {
                String id = billElement.getId();
                String customerName = billElement.getCustomerName();
                String customerMobile = billElement.getCustomerMobile();
                String createdDateTime = billElement.getCreatedDateTime();
                double money = billElement.getMoney();

                if(customerName == null) {
                    customerName = "";
                }
                if(customerMobile == null) {
                    customerMobile = "";
                }

                preparedStatement.setString(1, id);
                preparedStatement.setString(2, customerName);
                preparedStatement.setString(3, customerMobile);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(createdDateTime));
                preparedStatement.setDouble(5, money);
                status = preparedStatement.executeUpdate();
                totalProducts += billElement.getProducts().size();
            }
            System.out.println("Total products in the bills:" + totalProducts);
            System.out.println("================== Finished persisting bills: " + bills.size() + " ==================");
            System.out.println("================== Status: " + status + " ==================");
            connection.close();
            for(NhanhvnBill billElement: bills) {
            	persistNhanhvnBillProductDetails(billElement);
            }
            System.out.println("Finished persisting bill details: " + totalProducts);
        }
    }

    public void persistNhanhvnBillProductDetails(NhanhvnBill bill) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO nhanhvn_bill_details (quantity, price, billId, productId)" +
                    "VALUES(?,?,?,?)" +
                    "ON DUPLICATE KEY UPDATE" + 
                    " quantity = VALUES(quantity)," + 
                    " price = VALUES(price);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int totalChanges=0;
            for(NhanhvnBillProductDetail billDetailElement: bill.getProducts()) {
                float quantity = billDetailElement.getQuantity();
                double price = billDetailElement.getPrice();
            	String billId = bill.getId();
            	String productId = billDetailElement.getId();
            	
                preparedStatement.setFloat(1, quantity);
                preparedStatement.setDouble(2, price);
                preparedStatement.setString(3, billId);
                preparedStatement.setString(4, productId);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Finished persisting bill details: " + bill.getProducts().size() + " ==================");
            System.out.println("================== Total Changes: " + totalChanges + " ==================");
            connection.close();
        }
    }

}
