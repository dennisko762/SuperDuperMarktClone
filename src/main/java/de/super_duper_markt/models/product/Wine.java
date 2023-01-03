package de.super_duper_markt.models.product;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Wine extends BasicProduct implements Product {
    public Wine(String description, int quality, double basePrice, Type type) {
        super(description, quality, basePrice, type);
        this.setStorageDate(new Date());
    }

    @Override
    public boolean checkIfProductIsAddable() {
        return this.getQuality() >= 0;
    }

    @Override
    public double getDailyPrice(int quality) {
        return this.getBasePrice();
    }

    @Override
    public void updateQuality(Date currentDate) {
        long millisDiff = currentDate.toInstant().toEpochMilli() - this.getStorageDate().toInstant().toEpochMilli();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(millisDiff);
        if (daysDiff % 10 == 0 && daysDiff != 0 && this.getQuality() < 50) {
            this.setQuality(this.getQuality() + 1);
        }
    }

    @Override
    public boolean checkIfProductIsRemovable() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
