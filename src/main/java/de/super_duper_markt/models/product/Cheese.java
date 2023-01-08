package de.super_duper_markt.models.product;

import java.time.LocalDate;
import java.util.Date;

public class Cheese extends ExpirableProduct {
    private static final int MIN_QUALITY = 30;

    public Cheese(String description, int quality, double basePrice, Type type, LocalDate expiryDate, double maxStorageTemperatureCelsius) {
        super(description, quality, basePrice, type, maxStorageTemperatureCelsius);
        this.expiryDate = expiryDate;
    }

    @Override
    public double getDailyPrice() {
        return this.getBasePrice() + this.getQuality() * 0.1;
    }

    @Override
    public void updateQuality(Date currentDate) {
        this.setQuality(this.getQuality() - 1);
        this.getDailyPrice();
    }

    @Override
    public LocalDate getExpirationDate() {
        return expiryDate;
    }

    @Override
    public boolean checkIfProductIsAddable() {
        return this.getQuality() >= MIN_QUALITY && this.getExpirationDate().compareTo(LocalDate.now()) > 0;
    }

    @Override
    public boolean checkIfProductIsRemovable() {
        return !checkIfProductIsAddable();
    }
}
