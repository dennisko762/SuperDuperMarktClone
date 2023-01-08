package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.ExpirableProduct;
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

    private static final String COLUMN_DESCRIPTION = "description";
    private static final String QUALITY = "QUALITY";
    private static final String PRODUCT_ID = "productId";
    private static final String EXPIRATION_DATE = "expirationDate";
    private static final String BASE_PRICE = "basePrice";
    private static final String MAX_STORAGE_TEMPERATURE = "maxStorageTemperatureCelsius";

    private static final String TYPE = "type";

    public SQLProductSourceHandler(DataSourceType fileType) {
        super(fileType);
        initiateDatabaseConnection();
    }

    public void initiateDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost/TABLE" + "?user=USERNAME&password=PASSWORD");
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
                UUID productId = UUID.fromString(resultSet.getString(PRODUCT_ID));
                String description = resultSet.getString(COLUMN_DESCRIPTION);
                int quality = resultSet.getInt(QUALITY);
                double basePrice = resultSet.getDouble(BASE_PRICE);
                Type type = Type.valueOf(resultSet.getString(TYPE));
                String date = resultSet.getString(EXPIRATION_DATE);
                double maxStorageTemperature = resultSet.getDouble(MAX_STORAGE_TEMPERATURE);
                LocalDate expiryDate = null;
                if (date != null) {
                    expiryDate = LocalDate.parse(date);
                }

                BasicProduct product = ProductFactory.createProduct(type, description, basePrice, quality, expiryDate, maxStorageTemperature);
                product.setProductId(productId);
                products.add(product);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void addProduct(BasicProduct product) {

        try {
            String sql = "INSERT INTO products (description, quality, basePrice, productId, expirationDate, type, maxStorageTemperatureCelsius) VALUES (?, ?, ?, ?, ? ,?, ?)";
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, product.getDescription());
            statement.setInt(2, product.getQuality());
            statement.setDouble(3, product.getBasePrice());
            statement.setString(4, String.valueOf(product.getProductId()));
            statement.setString(6, String.valueOf(product.getType()));
            statement.setDouble(7, product.getMaxStorageTemperatureCelsius());


            if (product instanceof ExpirableProduct) {
                ExpirableProduct addableProduct = (ExpirableProduct) product;
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

                statement = this.connection.prepareStatement("INSERT INTO products (description, quality, basePrice, productId, expirationDate, type, maxStorageTemperatureCelsius) VALUES (?, ?, ?, ?, ? ,?, ?)  ON DUPLICATE KEY UPDATE quality = VALUES(quality)");

                statement.setObject(1, product.getDescription());
                statement.setObject(2, product.getQuality());
                statement.setObject(3, product.getBasePrice());
                statement.setObject(4, String.valueOf(product.getProductId()));
                if (product instanceof ExpirableProduct) {
                    ExpirableProduct addableProduct = (ExpirableProduct) product;
                    statement.setDate(5, java.sql.Date.valueOf((addableProduct.getExpirationDate())));
                } else {
                    statement.setNull(5, Types.DATE);
                }
                statement.setObject(6, String.valueOf(product.getType()));
                statement.setObject(7, product.getMaxStorageTemperatureCelsius());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
