package shared.persistence;

import gomhangvn.data.models.GomhangProduct;
import nhanhvn.data.models.*;
import nhanhvn.security.apistorage.ApiHelper;
import nhanhvn.security.apistorage.DatabaseCredentials;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection = null;

    private Connection makeDbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            DatabaseCredentials databaseCredentials = ApiHelper.getApiCredentials().getDatabaseDetails();
            String url = "jdbc:mysql://" + databaseCredentials.getHost() + ":" + databaseCredentials.getPort() +
                    "/" + databaseCredentials.getDatabaseName();
            connection = DriverManager.getConnection(url, databaseCredentials.getUsername(), databaseCredentials.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void persistNhanhvnProducts(List<NhanhvnProduct> products) throws SQLException {
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
            connection.close();
        }
    }

    public void persistGomhangvnProducts(List<GomhangProduct> products) throws SQLException {
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
                preparedStatement.setString(1,  id);
                preparedStatement.setString(2, name);
                totalChanges = preparedStatement.executeUpdate();
                System.out.println("Persisting product id: " + id + ", name: " + name);
            }
            connection.close();
        }
    }

    public void persistNhanhvnBills(List<NhanhvnBill> bills) throws SQLException {
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
            connection.close();
            for(NhanhvnBill billElement: bills) {
            	persistNhanhvnBillProductDetails(billElement);
            }
        }
    }

    public void persistNhanhvnBillProductDetails(NhanhvnBill bill) throws SQLException {
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
            connection.close();
        }
    }

    public void persistFacebookId(IdConversionObject idConversionObject) throws SQLException {
        connection = makeDbConnection();
        if(connection != null) {
            String facebookIdFromCsv = idConversionObject.getFacebookId();
            String idNhanhFromCsv = idConversionObject.getIdNhanh();
            String parentIdFromCsv = idConversionObject.getParentId();
            int parentIdValue, status = 0;
            if(!parentIdFromCsv.isEmpty()) {
                try {
                    parentIdValue = Integer.parseInt(parentIdFromCsv);
                    if (parentIdValue < 0 && !facebookIdFromCsv.isEmpty()) {
                        String sqlQuery = "UPDATE nhanhvn_product_list" +
                                " SET facebookId = ?" +
                                " WHERE idNhanh = ?" +
                                " OR parentId = ?;";
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                        preparedStatement.setString(1, facebookIdFromCsv);
                        preparedStatement.setString(2, idNhanhFromCsv);
                        preparedStatement.setString(3, idNhanhFromCsv);
                        status = preparedStatement.executeUpdate();
                    }
                } catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            connection.close();
            System.out.println("facebookid: " + facebookIdFromCsv + ", nhanhId: " + idNhanhFromCsv + "" +
                    ", total products mapped: " + status);
        }
    }

    public void updateFacebookIdFromProductTableToBillDetails() throws SQLException {
        connection = makeDbConnection();
        int status = 0;
        if (connection != null) {
            String sqlQuery = "UPDATE nhanhvn_bill_details bill " +
                    "SET bill.facebookId = " +
                    "(SELECT product.facebookId " +
                    "FROM nhanhvn_product_list product WHERE product.idNhanh = bill.productId);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            System.out.println("================== Mapping facebookId to bill details... ");
            status = preparedStatement.executeUpdate();
            System.out.println("================== Finished Mapping facebookId to bill details ");
        }
        System.out.println("Total products updated in bill details: " + status);
        connection.close();
    }

    public void updateBillUploadStatus(NhanhvnBill bill) throws SQLException {
        connection = makeDbConnection();
        int status = 0;
        if (connection != null) {
            String sqlQuery = "UPDATE nhanhvn_bills bills " +
                    "SET bills.facebookStatus = ? " +
                    "WHERE bills.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, bill.getFacebookStatus());
            preparedStatement.setString(2, bill.getId());
            System.out.println("================== Mapping upload status to bill " + bill.getId());
            status = preparedStatement.executeUpdate();
        }
        System.out.println("Update upload status: " + status);
        connection.close();
    }

    public NhanhvnBill getBillById(String billId) throws SQLException {
        NhanhvnBill bill = new NhanhvnBill();
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "SELECT * FROM nhanhvn_bills " +
                    "WHERE nhanhvn_bills.id = '" + billId + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            //preparedStatement.setString(1, bill.getId());

            ResultSet resultSet = preparedStatement.executeQuery(sqlQuery);
            while(resultSet.next()) {
                bill.setCreatedDateTime(resultSet.getTimestamp("createdDateTime").toString());
                bill.setCustomerMobile(resultSet.getString("customerMobile"));
                bill.setCustomerName(resultSet.getString("customerName"));
                bill.setId(resultSet.getString("id"));
                bill.setMoney(resultSet.getDouble("money"));
                bill.setFacebookStatus(resultSet.getBoolean("facebookStatus"));
            }
        }
        connection.close();
        return bill;
    }

    public NhanhvnExportProducts getNhanhvnParentProductsFromDb(boolean getFromAllBills) throws SQLException {
        NhanhvnExportProducts nhanhvnExportProducts = new NhanhvnExportProducts();
        connection = makeDbConnection();
        if (connection != null) {
            Statement st = connection.createStatement();
            String sqlQuery = "SELECT nhanhvn_product_list.idNhanh, " +
                    "nhanhvn_product_list.parentId, " +
                    "nhanhvn_product_list.productName, " +
                    "nhanhvn_product_list.facebookId, " +
                    "c.quantity " +
                    "FROM ( " +
                        "SELECT parentId, COUNT(*) AS quantity " +
                        "FROM (" +
                            "SELECT productId " +
                            "FROM nhanhvnstorage.nhanhvn_bill_details " +
                            "WHERE billId IN ( " +
                                "SELECT id " +
                                "FROM nhanhvnstorage.nhanhvn_bills " +
                                "WHERE createdDateTime > NOW() - INTERVAL 62 DAY" +
                            ")" +
                        ") a " +
                    "INNER JOIN (" +
                        "SELECT idNhanh, CASE WHEN parentId < 0 " +
                                        "THEN idNhanh " +
                                        "ELSE parentId END AS parentId " +
                        "FROM nhanhvn_product_list" +
                        ") b " +
                    "ON a.productId = b.idNhanh " +
                    "GROUP BY parentId " +
                    "ORDER BY COUNT(*) DESC) c " +
                    "LEFT JOIN nhanhvn_product_list " +
                    "ON c.parentId = nhanhvn_product_list.idNhanh";
            if (!getFromAllBills) {
                sqlQuery += " WHERE nhanhvn_product_list.facebookId = 0 OR nhanhvn_product_list.facebookId = -1";
            }
;
            ResultSet resultSet = st.executeQuery(sqlQuery);
            while (resultSet.next()) {
                NhanhvnExportProduct nhanhvnExportProduct = new NhanhvnExportProduct();
                String parentId = resultSet.getString("parentId");
                String name = resultSet.getString("productName");
                String idNhanh = resultSet.getString("idNhanh");
                String facebookId = resultSet.getString("facebookId");
                float quantity = resultSet.getFloat("quantity");

                nhanhvnExportProduct.setName(name);
                nhanhvnExportProduct.setIdNhanh(idNhanh);
                nhanhvnExportProduct.setFacebookId(facebookId);
                nhanhvnExportProduct.setParentId(parentId);
                nhanhvnExportProduct.setTotalProductsSold(quantity);
                nhanhvnExportProducts.getNhanhvnExportProductList().add(nhanhvnExportProduct);
            }
        }
        connection.close();
        return nhanhvnExportProducts;
    }

    private List<NhanhvnBillProductDetail> getBillDetailsFromDb() throws SQLException {
        List<NhanhvnBillProductDetail> billDetailList = new ArrayList<>();
        connection = makeDbConnection();
        if (connection != null) {
            Statement st = connection.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM nhanhvn_bill_details");
            while(resultSet.next()) {
                NhanhvnBillProductDetail  billDetails = new NhanhvnBillProductDetail();
                billDetails.setId(resultSet.getString("productId"));
                billDetails.setBillId(resultSet.getString("billId"));
                billDetails.setFacebookId(resultSet.getString("facebookId"));
                billDetails.setPrice(resultSet.getDouble("price"));
                billDetails.setQuantity(resultSet.getFloat("quantity"));
                billDetailList.add(billDetails);
            }
        }
        connection.close();
        return billDetailList;
    }

    public NhanhvnBills getBillsFromDb() throws SQLException {
        NhanhvnBills nhanhvnBills = new NhanhvnBills();
        List<NhanhvnBill> bills = nhanhvnBills.getNhanhvnBillList();
        connection = makeDbConnection();
        if (connection != null) {
            Statement st = connection.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM nhanhvn_bills WHERE customerMobile != '' " +
                    "AND facebookStatus = 0 " +
                    "AND createdDateTime > DATE(NOW()) - INTERVAL 62 DAY;");
            while(resultSet.next()) {
                NhanhvnBill  bill = new NhanhvnBill();
                bill.setCreatedDateTime(resultSet.getTimestamp("createdDateTime").toString());
                bill.setCustomerMobile(resultSet.getString("customerMobile"));
                bill.setCustomerName(resultSet.getString("customerName"));
                bill.setId(resultSet.getString("id"));
                bill.setMoney(resultSet.getDouble("money"));
                bill.setFacebookStatus(resultSet.getBoolean("facebookStatus"));
                bills.add(bill);
            }
        }
        connection.close();

        if (!bills.isEmpty()) {
            List<NhanhvnBillProductDetail> nhanhvnBillProductDetails = getBillDetailsFromDb();
            for (NhanhvnBill bill: bills) {
                for (NhanhvnBillProductDetail billDetail: nhanhvnBillProductDetails) {
                    if (billDetail.getBillId().equals(bill.getId())) {
                        bill.getProducts().add(new NhanhvnBillProductDetail(billDetail));
                    }
                }
            }
        }
        return nhanhvnBills;
    }
}


