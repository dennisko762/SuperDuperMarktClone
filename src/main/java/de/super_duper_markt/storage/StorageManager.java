package de.super_duper_markt.storage;

import de.super_duper_markt.data_management.data_source_handler.GenericProductSourceHandler;
import de.super_duper_markt.data_management.data_source_handler.ProductSourceHandlerFactory;
import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class StorageManager {
    private static StorageManager storageManagerInstance;
    private final List<BasicProduct> nonFreezerProducts;
    private static final double NON_FREEZER_STORAGE_TEMP = 7.0;
    private final List<FrozenProduct> freezableProducts;
    private static final double FREEZER_STORAGE_TEMP = 2.0;
    private final Calendar calendar;
    GenericProductSourceHandler productSourceHandler;

    private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);

    private StorageManager() {
        Date date = new Date();
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);

        this.nonFreezerProducts = new ArrayList<>();
        this.freezableProducts = new ArrayList<>();


        this.addProduct(new Cheese("Schimmelkäse", 50, 4.55, Type.CHEESE, LocalDate.now().plusDays(100), 7.0));
        this.addProduct(new Cheese("Evoi Pri", 37, 4.55, Type.CHEESE, LocalDate.now().plusDays(50), 7.0));
        this.addProduct(new Cheese("Mozarella", 55, 1.55, Type.CHEESE, LocalDate.now().plusDays(70), 7.0));
        this.addProduct(new Cheese("Camembert", 55, 1.55, Type.CHEESE, LocalDate.now().plusDays(0), 7.0));
        this.addProduct(new Cheese("Gauda", 29, 1.55, Type.CHEESE, LocalDate.now().plusDays(0), 7.0));
        this.addProduct(new Wine("Yellow Wine", 0, 14.55, Type.WINE, 15.0));
        this.addProduct(new Wine("Black WIne", 1, 9.55, Type.WINE, 15.0));
        this.addProduct(new Wine("Yellow Wine", -1, 14.55, Type.WINE, 15.0));
        this.addProduct(new Pizza("Dr. Oethker Speciale", 100, 3.55, Type.WINE, LocalDate.now().plusDays(300), 2.0));
        this.addProduct(new Pizza("Steinofen Salami", 30, 2.55, Type.WINE, LocalDate.now().plusDays(300), 2.0));
        this.addProduct(new Pizza("Dominos Pizza Funghi", -1, 14.55, Type.WINE, LocalDate.now().plusDays(300), -2.0));


        logger.info("Welcome to the Super Duper Supermarket!\n");
    }

    public static synchronized StorageManager getStorageManagerInstance() {
        if (storageManagerInstance == null) {
            storageManagerInstance = new StorageManager();
        }
        return storageManagerInstance;
    }


    public void initiateStorageManagement() {
        logger.info("Your storage contains the following products:\n");


        for (BasicProduct product : this.nonFreezerProducts) {
            logger.info(String.valueOf(product));
        }

        for (FrozenProduct frozenProduct : this.freezableProducts) {
            logger.info(String.valueOf(frozenProduct));
        }

        logger.info("Choose one of the following options:\n 1. Show product overview for the next n days\n 2. Import products from external source\n 3. Save products to external storage\n 4. Exit\n");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                logger.info("For how many days would you like to print your estimated storage? (in days)\n");
                int days = scanner.nextInt();
                showFutureOverview(days);
                initiateStorageManagement();
                break;
            case 2:
                this.addProductsFromExternalDataSource();
                initiateStorageManagement();
                break;
            case 3:
                saveProductsToExternalStorage();
                break;
            case 4:
                logger.info("Bye!");
                return;
            default:
                logger.info("The option you entered is not valid! \n");
                initiateStorageManagement();
        }
    }

    public void showFutureOverview(int days) {
        logger.info("Your estimated storage for the next " + days + " days looks as follows:");

        for (int i = 0; i <= days; i++) {
            this.calendar.add(Calendar.DATE, 1);
            logger.info(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));

            this.checkForRemovableProducts();
            updateProductQuality(this.nonFreezerProducts, this.calendar.getTime());
            updateProductQuality(this.freezableProducts, this.calendar.getTime());

            for (BasicProduct product : this.nonFreezerProducts) {
                logger.info(String.valueOf(product));
            }

            for (FrozenProduct frozenProduct : this.freezableProducts) {
                logger.info(String.valueOf(frozenProduct));
            }
        }
    }

    public void addProduct(BasicProduct product) {
        if (product.checkIfProductIsAddable()) {
            if (product instanceof FrozenProduct) {
                if (product.getMaxStorageTemperatureCelsius() >= FREEZER_STORAGE_TEMP) {
                    freezableProducts.add((FrozenProduct) product);
                }
            } else {
                if (product.getMaxStorageTemperatureCelsius() >= NON_FREEZER_STORAGE_TEMP) {
                    nonFreezerProducts.add(product);
                }
            }
            logger.info("Product: " + product.getDescription() + ", " + product.getProductId() + " was successfully added to the storage\n");

        } else if (!product.checkIfProductIsAddable()) {
            logger.info("This product " + product.getDescription() + " does not fulfill the requirements and will not be added to the storage!\n");
        } else {
            logger.info("The productId " + product.getProductId() + " already exists in the storage\n");
        }

    }


    public void removeProduct(List<? extends BasicProduct> removableProducts) {
        for (BasicProduct product : removableProducts) {
            if (product instanceof FrozenProduct) {
                this.freezableProducts.remove(product);
            } else {
                this.nonFreezerProducts.remove(product);
            }
        }

    }

    public void checkForRemovableProducts() {
        List<BasicProduct> found = new ArrayList<>();
        for (BasicProduct product : this.nonFreezerProducts) {
            if (product.checkIfProductIsRemovable()) {
                found.add(product);
            }
        }

        for (FrozenProduct frozenProduct : this.freezableProducts) {
            if (frozenProduct.checkIfProductIsRemovable()) {
                found.add(frozenProduct);
            }
        }

        if (found.isEmpty()) {
            logger.info("The products " + found + " have expired.\n Would you like to remove it from the shelf?\n 1. y (yes)\n 2. n (no)\n");
            Scanner scanner = new Scanner(System.in);
            String removeProduct = scanner.nextLine();
            switch (removeProduct) {
                case "y":
                    this.removeProduct(found);
                    return;
                case "n":
                    return;
                default:
                    logger.info("The answer you entered is invalid. Pls try again.");
                    checkForRemovableProducts();
            }
        }

    }

    public void updateProductQuality(List<? extends BasicProduct> products, Date currentDate) {
        for (BasicProduct product : products) {
            product.updateQuality(currentDate);
        }
    }

    public void addProductsFromExternalDataSource() {
        logger.info("Which of the following types would you like to add the product data from \n 1. SQL\n 2. CSV\n");
        Scanner scanner = new Scanner(System.in);
        DataSourceType dataSourceType = DataSourceType.valueOf(scanner.nextLine());
        this.productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(dataSourceType);

        if (productSourceHandler.getFileType() == dataSourceType) {
            List<? extends BasicProduct> externallyLoadedProducts = this.productSourceHandler.getProducts();
            for (BasicProduct externallyAddedProduct : externallyLoadedProducts) {
                this.addProduct(externallyAddedProduct);
            }
        }
    }

    public void saveProductsToExternalStorage() {
        logger.info("Which type of storage would you like to save your data to?\n");
        Scanner scanner = new Scanner(System.in);
        DataSourceType dataSourceType = DataSourceType.valueOf(scanner.nextLine());
        this.productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(dataSourceType);
        List<BasicProduct> totalList = new ArrayList<>();
        totalList.addAll(nonFreezerProducts);
        totalList.addAll(freezableProducts);
        this.productSourceHandler.saveProducts(totalList);
        this.initiateStorageManagement();
    }
}
