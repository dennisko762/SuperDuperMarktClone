package de.super_duper_markt.models.product;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Pizza extends FrozenProduct {

    private static final int MIN_QUALITY = 50;

    public Pizza(String description, int quality, double basePrice, Type type, LocalDate expiryDate, double maxStorageTemperatureCelsius) {
        super(description, quality, basePrice, type, maxStorageTemperatureCelsius);
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean checkIfProductIsAddable() {
        return this.expiryDate.compareTo(LocalDate.now()) > 0 && MIN_QUALITY <= this.getQuality();
    }

    @Override
    public boolean checkIfProductIsRemovable() {
        return !this.checkIfProductIsAddable();
    }

    @Override
    public double getDailyPrice() {
        return this.getBasePrice() + this.getQuality() * 0.7;
    }

    @Override
    public void updateQuality(Date currentDate) {
        long millisDiff = currentDate.toInstant().toEpochMilli() - this.getStorageDate().toInstant().toEpochMilli();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(millisDiff);
        if (daysDiff % 30 == 0 && daysDiff != 0) {
            this.setQuality(this.getQuality() - 1);
        }
    }
}
