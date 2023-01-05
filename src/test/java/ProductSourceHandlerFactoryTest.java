import de.super_duper_markt.data_management.data_source_handler.CSVProductSourceHandler;
import de.super_duper_markt.data_management.data_source_handler.ProductSourceHandlerFactory;
import de.super_duper_markt.data_management.data_source_handler.SQLProductSourceHandler;
import de.super_duper_markt.data_management.models.DataSourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class ProductSourceHandlerFactoryTest {

    @Test
    @DisplayName("ProductSourceHandlerFactory should return correct Handler class")
    void tesCreateProductSourceHandler() {
        DataSourceType fileType = DataSourceType.SQL;
        assertInstanceOf(SQLProductSourceHandler.class, ProductSourceHandlerFactory.createProductSourceHandler(fileType));

        fileType = DataSourceType.CSV;
        assertInstanceOf(CSVProductSourceHandler.class, ProductSourceHandlerFactory.createProductSourceHandler(fileType));


    }
}
