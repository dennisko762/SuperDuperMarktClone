package de.super_duper_markt.data_management.data_source_handler;

import de.super_duper_markt.data_management.models.DataSourceType;
import org.jetbrains.annotations.NotNull;

public class ProductSourceHandlerFactory {

    public static GenericProductSourceHandler createProductSourceHandler(@NotNull DataSourceType fileType) {
        switch (fileType) {
            case SQL:
                return new SQLProductSourceHandler(fileType);
            case CSV:
                String filePath = "C:\\Users\\Dennis\\Desktop\\products.csv";
                return new CSVProductSourceHandler(fileType, filePath);
            case CODE:
                return new JavaProductSourceHandler(fileType);
            default:
                throw new IllegalArgumentException("The provided file type " + fileType + "is not valid!");
        }
    }
}
