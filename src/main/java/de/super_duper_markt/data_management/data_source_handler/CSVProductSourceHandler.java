package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.ExpirableProduct;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.factory.ProductFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CSVProductSourceHandler extends GenericProductSourceHandler {
    private final String filePath;
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;

    private static final Logger logger = LoggerFactory.getLogger(CSVProductSourceHandler.class);

    private static final String COLUMN_DESCRIPTION = "description";
    private static final String QUALITY = "QUALITY";
    private static final String PRODUCT_ID = "productId";
    private static final String EXPIRATION_DATE = "expirationDate";
    private static final String BASE_PRICE = "basePrice";
    private static final String MAX_STORAGE_TEMPERATURE = "maxStorageTemperatureCelsius";

    private static final String TYPE = "type";

    public CSVProductSourceHandler(DataSourceType filetype, String filePath) {
        super(filetype);
        this.filePath = filePath;
    }

    @Override
    public List<BasicProduct> getProducts() {
        FileReader fileReader;
        List<BasicProduct> products = new ArrayList<>();

        try {
            fileReader = new FileReader(this.filePath);
            CSVParser csvParser = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(fileReader);

            for (CSVRecord csvRecord : csvParser) {
                String description = csvRecord.get(COLUMN_DESCRIPTION);
                String quality = csvRecord.get(QUALITY);
                String unparsedExpiryDate = csvRecord.get(EXPIRATION_DATE);
                LocalDate expiryDate;

                if (!unparsedExpiryDate.equals("NULL")) {
                    expiryDate = LocalDate.parse(unparsedExpiryDate);
                } else {
                    expiryDate = null;
                }
                double basePrice = Double.parseDouble((csvRecord.get(BASE_PRICE)));
                Type type = Type.valueOf(csvRecord.get(TYPE));
                double maxStorageTemperature = Double.parseDouble(csvRecord.get(MAX_STORAGE_TEMPERATURE));
                BasicProduct basicProduct = ProductFactory.createProduct(type, description, basePrice, Integer.parseInt(quality), expiryDate, maxStorageTemperature);
                basicProduct.setProductId(UUID.fromString(csvRecord.get(PRODUCT_ID)));
                products.add(basicProduct);
            }
            csvParser.close();
            fileReader.close();
        } catch (IOException | DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    @Override
    public void addProduct(@NotNull BasicProduct product) {
        try {
            this.fileWriter = new FileWriter(this.filePath, true);
            this.csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180.builder().setHeader(COLUMN_DESCRIPTION, QUALITY, BASE_PRICE, PRODUCT_ID, EXPIRATION_DATE, TYPE, MAX_STORAGE_TEMPERATURE).setDelimiter(',').setSkipHeaderRecord(true).build());

            if (product.checkIfProductIsAddable()) {
                csvPrinter.printRecord(product);
            } else {
                logger.error("Sorry, the product: " + product.getDescription() + " doesnt meet the requirements to be added to the storage");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveProducts(@NotNull List<BasicProduct> basicProducts) {
        try {
            this.fileWriter = new FileWriter(this.filePath);
            this.csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180.builder().setDelimiter(',').setHeader(COLUMN_DESCRIPTION, QUALITY, BASE_PRICE, PRODUCT_ID, EXPIRATION_DATE, TYPE, MAX_STORAGE_TEMPERATURE).build());

            basicProducts.forEach(product -> {
                try {
                    String date;
                    if (product instanceof ExpirableProduct) {
                        date = String.valueOf(((ExpirableProduct) product).getExpirationDate());
                    } else {
                        date = "NULL";
                    }
                    csvPrinter.printRecord(product.getDescription(), product.getQuality(), product.getBasePrice(), product.getProductId(), date, product.getType());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            this.csvPrinter.close();
            this.fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
