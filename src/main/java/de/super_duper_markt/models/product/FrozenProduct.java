package de.super_duper_markt.models.product;

import java.time.LocalDate;

public abstract class FrozenProduct extends ExpirableProduct {

    private final double maxStorageTemperatureCelsius;

    protected FrozenProduct(String description, int quality, double basePrice, Type type, double maxStorageTemperatureCelsius) {
        super(description, quality, basePrice, type, maxStorageTemperatureCelsius);
        this.maxStorageTemperatureCelsius = maxStorageTemperatureCelsius;
    }

    @Override
    public double getMaxStorageTemperatureCelsius() {
        return maxStorageTemperatureCelsius;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expiryDate;
    }
}
