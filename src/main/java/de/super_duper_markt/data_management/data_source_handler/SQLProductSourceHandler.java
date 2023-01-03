package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.Expirable;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.factory.ProductFactory;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SQLProductSourceHandler extends GenericProductSourceHandler {

    private Connection connection;

    public SQLProductSourceHandler(DataSourceType fileType) {
        super(fileType);
        initiateDatabaseConnection();
    }

    public void initiateDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost/superdupermarket" + "?user=root&password=Deprecla12claya12");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BasicProduct> getProducts() {
        List<BasicProduct> products = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM products");

            while (resultSet.next()) {
                UUID productId = UUID.fromString(resultSet.getString("productId"));
                String description = resultSet.getString("description");
                int quality = resultSet.getInt("quality");
                double basePrice = resultSet.getDouble("basePrice");
                Type type = Type.valueOf(resultSet.getString("type"));
                String date = resultSet.getString("expirationDate");

                LocalDate expiryDate = null;
                if (date != null) {
                    expiryDate = LocalDate.parse(date);
                }

                BasicProduct product = ProductFactory.createProduct(type, description, basePrice, quality, expiryDate);
                product.setProductId(productId);
                products.add(product);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println();
        return products;
    }

    @Override
    public void addProduct(BasicProduct product) {

        try {
            String sql = "INSERT INTO products (description, quality, basePrice, productId, expirationDate, type) VALUES (?, ?, ?, ?, ? ,?)";
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, product.getDescription());
            statement.setInt(2, product.getQuality());
            statement.setDouble(3, product.getBasePrice());
            statement.setString(4, String.valueOf(product.getProductId()));
            statement.setString(6, String.valueOf(product.getType()));

            if (product instanceof Expirable) {
                Expirable addableProduct = (Expirable) product;
                statement.setDate(5, java.sql.Date.valueOf((addableProduct.getExpirationDate())));
            } else {
                statement.setNull(5, Types.DATE);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void saveProducts(@NotNull List<BasicProduct> basicProducts) {

        String productIds = basicProducts.stream().map(BasicProduct::getProductId).collect(Collectors.toList()).toString();
        PreparedStatement deleteRemovedProductsStatement;
        try {
            deleteRemovedProductsStatement = this.connection.prepareStatement("DELETE FROM products WHERE productId NOT IN (?)");
            deleteRemovedProductsStatement.setString(1, productIds);
            deleteRemovedProductsStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        PreparedStatement statement;
        for (BasicProduct product : basicProducts) {
            try {

                statement = this.connection.prepareStatement("INSERT INTO products (description, quality, basePrice, productId, expirationDate, type) VALUES (?, ?, ?, ?, ? ,?)  ON DUPLICATE KEY UPDATE quality = VALUES(quality)");

                statement.setObject(1, product.getDescription());
                statement.setObject(2, product.getQuality());
                statement.setObject(3, product.getBasePrice());
                statement.setObject(4, String.valueOf(product.getProductId()));
                if (product instanceof Expirable) {
                    Expirable addableProduct = (Expirable) product;
                    statement.setDate(5, java.sql.Date.valueOf((addableProduct.getExpirationDate())));
                } else {
                    statement.setNull(5, Types.DATE);
                }
                statement.setObject(6, String.valueOf(product.getType()));
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
