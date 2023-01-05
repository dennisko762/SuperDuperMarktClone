package de.super_duper_markt.models.product;

import java.time.LocalDate;

public abstract class FrozenProduct extends ExpirableProduct implements Storable {

    private final double maxStorageTemperatureCelsius;

    public FrozenProduct(String description, int quality, double basePrice, Type type, double maxStorageTemperatureCelsius) {
        super(description, quality, basePrice, type, maxStorageTemperatureCelsius);
        this.maxStorageTemperatureCelsius = maxStorageTemperatureCelsius;
    }

    public double getMaxStorageTemperatureCelsius() {
        return maxStorageTemperatureCelsius;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expiryDate;
    }
}
