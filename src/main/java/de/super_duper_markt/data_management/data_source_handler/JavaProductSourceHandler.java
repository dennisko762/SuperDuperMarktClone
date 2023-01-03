package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.Product;
import java.util.ArrayList;
import java.util.List;

public class JavaProductSourceHandler extends GenericProductSourceHandler {

    private List<BasicProduct> products = new ArrayList<>();

    public JavaProductSourceHandler(DataSourceType fileType) {
        super(fileType);
    }

    @Override
    public List<BasicProduct> getProducts() {
        return this.products;
    }

    @Override
    public void addProduct(BasicProduct product) {
        if (product instanceof Product) {
            Product addableProduct = (Product) product;
            if (addableProduct.checkIfProductIsAddable()) {
                this.products.add(product);
            } else {
                System.out.println("Sorry, the product: " + product.getDescription() + " doesnt meet the requirements to be added to the storage");
            }
        } else {
            System.out.println("Sorry, the provided product doesnt seem to implement the given interface");
        }
    }

    @Override
    public void saveProducts(List<BasicProduct> basicProducts ) {
        this.products = basicProducts;
    }

}
