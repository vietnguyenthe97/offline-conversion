package nhanhvn.data.persistence;

import nhanhvn.data.model.Product;
import nhanhvn.data.service.ProductDataService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanhvnDatabaseConnection {
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nhanhvn_products", "root", "langthanG*1992");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void persistAllProducts(ProductDataService productDataService) throws SQLException, IOException {
        connection = makeDbConnection();
        if (connection != null) {
            String sqlQuery = "INSERT INTO product_list (idNhanh, productName, parentId)" +
                    "VALUES(?,?,?)" +
                    "ON DUPLICATE KEY UPDATE productName = VALUES(productName);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            productDataService.getAllProducts();
            List<Product> products = new ArrayList<>();
            products = productDataService.getProducts().getProductList();
            int totalChanges=0;
            for(Product productElement: products) {
                String idNhanh = productElement.getIdNhanh();
                String name = productElement.getName();
                String parentId = productElement.getParentId();
                preparedStatement.setString(1, idNhanh);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, parentId);
                totalChanges = preparedStatement.executeUpdate();
            }
            System.out.println("================== Total Changes: " + totalChanges + "==================");
            connection.close();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        NhanhvnDatabaseConnection dbConn = new NhanhvnDatabaseConnection();
        ProductDataService productDataService = new ProductDataService();
        dbConn.persistAllProducts(productDataService);
    }
}
