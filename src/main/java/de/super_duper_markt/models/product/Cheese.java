package de.super_duper_markt.models.product;

import java.time.LocalDate;
import java.util.Date;

public class Cheese extends BasicProduct implements Expirable, Product {
    private static final int MIN_QUALITY = 30;
    private final LocalDate expiryDate;

    public Cheese(String description, int quality, double basePrice, Type type, LocalDate expiryDate) {
        super(description, quality, basePrice, type);
        this.expiryDate = expiryDate;
    }

    @Override
    public double getDailyPrice(int quality) {
        return this.getBasePrice() + quality * 0.1;
    }

    @Override
    public void updateQuality(Date currentDate) {
        this.setQuality(this.getQuality() - 1);
        this.getDailyPrice(this.getQuality());
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

    @Override
    public String toString() {
        String productInfo = ", has to be removed=";
        if (checkIfProductIsRemovable()) {
            productInfo += "yes";
        } else {
            productInfo += "no";
        }
        return super.toString() +
                productInfo;
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
