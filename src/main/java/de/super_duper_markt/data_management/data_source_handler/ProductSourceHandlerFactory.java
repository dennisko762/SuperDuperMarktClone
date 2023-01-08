package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import org.jetbrains.annotations.NotNull;

public class ProductSourceHandlerFactory {

    private ProductSourceHandlerFactory() {
    }

    public static GenericProductSourceHandler createProductSourceHandler(@NotNull DataSourceType fileType) {
        switch (fileType) {
            case SQL:
                return new SQLProductSourceHandler(fileType);
            case CSV:
                String filePath = "";
                return new CSVProductSourceHandler(fileType, filePath);
            default:
                throw new IllegalArgumentException("The provided file type " + fileType + "is not valid!");
        }
    }
}
