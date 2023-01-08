package de.super_duper_markt.models.product;

import java.time.LocalDate;

public abstract class ExpirableProduct extends BasicProduct {
    LocalDate expiryDate;

    protected ExpirableProduct(String description, int quality, double basePrice, Type type, double maxStorageTemperatureCelsius) {
        super(description, quality, basePrice, type, maxStorageTemperatureCelsius);
    }

    public LocalDate getExpirationDate() {
        return this.expiryDate;
    }

    @Override
    public String toString() {
        if (checkIfProductIsRemovable()) {
            return super.toString().concat(", has to be removed= yes");
        } else {
            return super.toString().concat(", has to be removed= no");
        }
    }
}
