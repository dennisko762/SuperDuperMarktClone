package de.super_duper_markt.storage;

import de.super_duper_markt.data_management.data_source_handler.GenericProductSourceHandler;
import de.super_duper_markt.data_management.data_source_handler.ProductSourceHandlerFactory;
import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class StorageManager {
    private static StorageManager storageManagerInstance;
    private final List<BasicProduct> products;
    private final Calendar calendar;
    GenericProductSourceHandler productSourceHandler;

    private StorageManager() {
        Date date = new Date();
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);

        this.products = new ArrayList<>();
        this.addProduct(new Cheese("Schimmelkäse", 50, 4.55, Type.CHEESE, LocalDate.now().plusDays(100)));
        this.addProduct(new Cheese("Evoi Pri", 37, 4.55, Type.CHEESE, LocalDate.now().plusDays(50)));
        this.addProduct(new Cheese("Mozarella", 55, 1.55, Type.CHEESE, LocalDate.now().plusDays(70)));
        this.addProduct(new Cheese("Camembert", 55, 1.55, Type.CHEESE, LocalDate.now().plusDays(0)));
        this.addProduct(new Cheese("Gauda", 29, 1.55, Type.CHEESE, LocalDate.now().plusDays(0)));
        this.addProduct(new Wine("Yellow Wine", 0, 14.55, Type.WINE));
        this.addProduct(new Wine("Black WIne", 1, 9.55, Type.WINE));
        this.addProduct(new Wine("Yellow Wine", -1, 14.55, Type.WINE));


        System.out.println("Welcome to the Super Duper Supermarket!\n");
        System.out.println("Your storage contains the following products:\n");

        for (BasicProduct product : this.products) {
            System.out.println(product + "\n");
        }
    }

    public static synchronized StorageManager getStorageManagerInstance() {
        if (storageManagerInstance == null) {
            storageManagerInstance = new StorageManager();
        }
        return storageManagerInstance;
    }


    public void initiateStorageManagement() {

        System.out.println("Choose one of the following options:\n 1. Show product overview for the next n days\n 2. Import products from external source\n 3. Save products to external storage\n 4. Exit\n");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                System.out.println("For how many days would you like to print your estimated storage? (in days)\n");
                int days = scanner.nextInt();
                showFutureOverview(days);
                initiateStorageManagement();
                break;
            case 2:
                try {
                    this.addProductsFromExternalDataSource();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                initiateStorageManagement();
                break;
            case 3:
                saveProductsToExternalStorage();
                break;
            case 4:
                System.out.println("Bye!");
                return;
            default:
                System.out.println("The option you entered is not valid! \n");
                initiateStorageManagement();
        }
    }

    public void showFutureOverview(int days) {
        System.out.println("Your estimated storage for the next " + days + " days looks as follows:\n");

        for (int i = 0; i <= days; i++) {
            this.calendar.add(Calendar.DATE, 1);
            System.out.println(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + "\n");

            this.checkForRemovableProducts();
            updateProductQuality(this.products, this.calendar.getTime());

            for (BasicProduct product : this.products) {
                System.out.println(product + "\n");
            }
        }
    }

    public void addProduct(BasicProduct product) {
        if (product instanceof Product) {
            if (((Product) product).checkIfProductIsAddable() && !this.products.contains(product)) {
                this.products.add(product);
                System.out.println("Product: " + product.getDescription() + ", " + product.getProductId() + " was successfully added to the storage\n");

            } else if (!((Product) product).checkIfProductIsAddable()) {
                System.out.println("This product " + product.getDescription() + " does not fulfill the requirements and will not be added to the storage!\n");
            } else {
                System.out.println("The productId " + product.getProductId() + " already exists in the storage\n");
            }
        }
    }


    public void removeProduct(List<BasicProduct> removableProducts) {
        this.products.removeAll(removableProducts);
    }

    public void checkForRemovableProducts() {
        List<BasicProduct> found = new ArrayList<>();
        for (BasicProduct product : this.products) {
            if (product instanceof Product) {
                if (((Product) product).checkIfProductIsRemovable()) {
                    found.add(product);
                }
            }
        }

        if (found.size() > 0) {
            System.out.println("The products " + found + " have expired.\n Would you like to remove it from the shelf?\n 1. y (yes)\n 2. n (no)\n");
            Scanner scanner = new Scanner(System.in);
            String removeProduct = scanner.nextLine();
            switch (removeProduct) {
                case "y":
                    this.removeProduct(found);
                    return;
                case "n":
                    return;
                default:
                    System.out.println("The answer you entered is invalid. Pls try again.");
                    checkForRemovableProducts();
            }
        }

    }

    public void updateProductQuality(List<BasicProduct> products, Date currentDate) {
        for (BasicProduct product : products) {
            if (product instanceof Product) {
                ((Product) product).updateQuality(currentDate);
            }
        }
    }

    public void addProductsFromExternalDataSource() throws SQLException {
        System.out.println("Which of the following types would you like to add the product data from \n 1. SQL\n 2. CSV\n");
        Scanner scanner = new Scanner(System.in);
        DataSourceType dataSourceType = DataSourceType.valueOf(scanner.nextLine());
        this.productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(dataSourceType);

        if (productSourceHandler.getFileType() == dataSourceType) {
            List<BasicProduct> externallyLoadedProducts = this.productSourceHandler.getProducts();
            for (BasicProduct externallyAddedProduct : externallyLoadedProducts) {
                this.addProduct(externallyAddedProduct);
            }

        }
        for (BasicProduct product : this.products) {
            System.out.println(product + "\n");
        }
    }

    public void saveProductsToExternalStorage() {
        System.out.println("Which type of storage would you like to save your data to?\n");
        Scanner scanner = new Scanner(System.in);
        DataSourceType dataSourceType = DataSourceType.valueOf(scanner.nextLine());
        this.productSourceHandler = ProductSourceHandlerFactory.createProductSourceHandler(dataSourceType);
        this.productSourceHandler.saveProducts(this.products);
        this.initiateStorageManagement();
    }
}
