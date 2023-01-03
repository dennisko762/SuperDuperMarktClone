package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import de.super_duper_markt.models.product.BasicProduct;

import java.util.List;

public abstract class GenericProductSourceHandler {
    private final DataSourceType fileType;

    protected GenericProductSourceHandler(DataSourceType fileType) {
        this.fileType = fileType;
    }

    public DataSourceType getFileType() {
        return this.fileType;
    }

    public abstract List<BasicProduct> getProducts();

    public abstract void addProduct(BasicProduct product);

    public abstract void saveProducts(List<BasicProduct> products);


}

