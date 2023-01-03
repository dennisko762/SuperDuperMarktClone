package de.super_duper_markt.models.product.factory;

import de.super_duper_markt.models.product.BasicProduct;
import de.super_duper_markt.models.product.Cheese;
import de.super_duper_markt.models.product.Type;
import de.super_duper_markt.models.product.Wine;

import java.time.LocalDate;
public class ProductFactory {

    public static BasicProduct createProduct(Type type, String description, double basePrice, int quality, LocalDate expirationDate) {
        switch (type) {
            case CHEESE:
                return new Cheese(description, quality, basePrice, type, expirationDate);
            case WINE:
                return new Wine(description, quality, basePrice, type);
            default:
                throw new IllegalArgumentException("Invalid object type: " + type);
        }
    }
}
