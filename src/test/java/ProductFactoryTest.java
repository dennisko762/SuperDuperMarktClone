import de.super_duper_markt.models.product.Cheese;
import de.super_duper_markt.models.product.Pizza;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.Wine;
import de.super_duper_markt.models.product.factory.ProductFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ProductFactoryTest {

    @Test
    @DisplayName("ProductFactory should return correct Product class")
    void createProductTest() {
        Type productType = Type.CHEESE;
        assertInstanceOf(Cheese.class, ProductFactory.createProduct(productType, "test-description", 0.00, 100, LocalDate.now(), 7.0));

        productType = Type.WINE;
        assertInstanceOf(Wine.class, ProductFactory.createProduct(productType, "test-description", 0.00, 100, LocalDate.now(), 7.0));

        productType = Type.PIZZA;
        assertInstanceOf(Pizza.class, ProductFactory.createProduct(productType, "test-description", 0.00, 100, LocalDate.now(), 2.0));


    }
}
