import de.super_duper_markt.data_management.data_source_handler.GenericProductSourceHandler;
import de.super_duper_markt.data_management.data_source_handler.ProductSourceHandlerFactory;
import de.super_duper_markt.data_management.data_source_handler.SQLProductSourceHandler;
import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.Cheese;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.Wine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StoreManagerTest {

    List<BasicProduct> productList = new ArrayList<>();

    @BeforeEach
    public void init() {
        BasicProduct cheeseTestProduct = new Cheese("Mozzarella", 49, 3.45, Type.CHEESE, LocalDate.now().plusDays(55), 7.0);
        BasicProduct wineTestProduct = new Wine("Burgunder", 12, 7.45, Type.WINE, 13.0);
        this.productList.add(cheeseTestProduct);
        this.productList.add(wineTestProduct);
    }


    @Test
    @DisplayName("Should add valid products to the list")
    void addProducts() {
        GenericProductSourceHandler productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(DataSourceType.SQL);
        BasicProduct cheeseTestProduct = new Cheese("Mozzarella", 100, 3.45, Type.CHEESE, LocalDate.now().plusDays(55), 7.0);
        BasicProduct wineTestProduct = new Wine("Burgunder", 12, 7.45, Type.WINE, 17.0);

        productSourceHandler.addProduct(cheeseTestProduct);
        productSourceHandler.addProduct(wineTestProduct);

        this.productList = productSourceHandler.getProducts();

        assertTrue(productList.contains(cheeseTestProduct));
        assertTrue(productList.contains(wineTestProduct));
    }

    @Test
    @DisplayName("Should not add invalid products to the list")
    void addInvalidProducts() {
        BasicProduct cheeseTestProduct = new Cheese("Mozzarella", 29, 3.45, Type.CHEESE, LocalDate.now().plusDays(55), 7.0);
        BasicProduct cheeseTestProductExpired = new Cheese("Mozzarella", 29, 3.45, Type.CHEESE, LocalDate.now().minusDays(5), 7.0);

        if (cheeseTestProduct.checkIfProductIsAddable()) {
            this.productList.add(cheeseTestProduct);
        }

        if (cheeseTestProductExpired.checkIfProductIsAddable()) {
            this.productList.add(cheeseTestProductExpired);
        }


        assertFalse(productList.contains(cheeseTestProduct));
        assertFalse(productList.contains(cheeseTestProductExpired));
    }

    @Test
    @DisplayName("Should update product quality")
    void updateProductQuality() {
        GenericProductSourceHandler productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(DataSourceType.SQL);
        BasicProduct cheeseTestProduct = new Cheese("Mozzarella", 49, 3.45, Type.CHEESE, LocalDate.now().plusDays(55), 7.0);
        BasicProduct wineTestProduct = new Wine("Burgunder", 12, 7.45, Type.WINE, 15.0);

        productSourceHandler.addProduct(cheeseTestProduct);
        productSourceHandler.addProduct(wineTestProduct);
        this.productList = productSourceHandler.getProducts();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int days = 10;
        for (int i = 1; i <= days; i++) {
            for (BasicProduct product : this.productList) {
                product.updateQuality(Calendar.getInstance().getTime());
            }

            this.productList = productSourceHandler.getProducts();
            calendar.add(Calendar.DATE, 1);
        }
        int cheeseTestProductIndex = productList.indexOf(cheeseTestProduct);
        int wineTestProductProductIndex = productList.indexOf(wineTestProduct);

        assertEquals(49, productList.get(cheeseTestProductIndex).getQuality());
        assertEquals(12, productList.get(wineTestProductProductIndex).getQuality());
    }


    @Test
    @DisplayName("Should save to external data source")
    void testSaveProducts() throws ClassNotFoundException, SQLException {
        GenericProductSourceHandler productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(DataSourceType.SQL);
        SQLProductSourceHandler sqlProductSourceHandler = (SQLProductSourceHandler) productSourceHandler;
        Connection connection;

        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/superdupermarket", "root", "Deprecla12claya12");
        sqlProductSourceHandler.setConnection(connection);
        System.out.println(this.productList);
        sqlProductSourceHandler.saveProducts(this.productList);

        List<BasicProduct> list = productSourceHandler.getProducts();
        List<BasicProduct> sortedListFromExternalSource = list.stream().sorted(Comparator.comparing(BasicProduct::getDescription)).collect(Collectors.toList());
        List<BasicProduct> sortedList = this.productList.stream().sorted(Comparator.comparing(BasicProduct::getDescription)).collect(Collectors.toList());


        assertTrue(sortedList.containsAll(sortedListFromExternalSource));

    }
}
