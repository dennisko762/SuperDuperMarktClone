package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.Expirable;
import de.super_duper_markt.models.product.Product;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.factory.ProductFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

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

            for (CSVRecord record : csvParser) {
                String description = record.get("description");
                String quality = record.get("quality");
                String unparsedExpiryDate = record.get("expirationDate");
                LocalDate expiryDate;

                if (!unparsedExpiryDate.equals("NULL")) {
                    expiryDate = LocalDate.parse(unparsedExpiryDate);
                } else {
                    expiryDate = null;
                }
                double basePrice = Double.parseDouble((record.get("basePrice")));
                Type type = Type.valueOf(record.get("type"));
                BasicProduct basicProduct = ProductFactory.createProduct(type, description, basePrice, Integer.parseInt(quality), expiryDate);
                basicProduct.setProductId(UUID.fromString(record.get("productId")));
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
            this.csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180.builder().setHeader("description", "quality", "basePrice", "productId", "expirationDate", "type").setDelimiter(',').setSkipHeaderRecord(true).build());

            if (product instanceof Product) {
                Product addableProduct = (Product) product;
                if (addableProduct.checkIfProductIsAddable()) {
                    csvPrinter.printRecord(product);
                } else {
                    System.out.println("Sorry, the product: " + product.getDescription() + " doesnt meet the requirements to be added to the storage");
                }
            } else {
                System.out.println("Sorry, the provided product doesnt seem to implement the given interface");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveProducts(@NotNull List<BasicProduct> basicProducts) {

        try {
            this.fileWriter = new FileWriter(this.filePath);
            this.csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180.builder().setDelimiter(',').setHeader("description", "quality", "basePrice", "productId", "expirationDate", "type").build());

            basicProducts.forEach(product -> {
                try {
                    String date;
                    if (product instanceof Expirable) {
                        date = String.valueOf(((Expirable) product).getExpirationDate());
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
