package de.super_duper_markt.models.product.factory;

import de.super_duper_markt.models.product.*;

import java.time.LocalDate;
public class ProductFactory {

    public static BasicProduct createProduct(Type type, String description, double basePrice, int quality, LocalDate expirationDate, double maxStorageTemperature) {
        switch (type) {
            case CHEESE:
                return new Cheese(description, quality, basePrice, type, expirationDate, maxStorageTemperature);
            case WINE:
                return new Wine(description, quality, basePrice, type, maxStorageTemperature);
            case PIZZA:
                return new Pizza(description, quality, basePrice, type, expirationDate, maxStorageTemperature);
            default:
                throw new IllegalArgumentException("Invalid object type: " + type);
        }
    }
}
