package de.super_duper_markt.models.product;

import java.time.LocalDate;

public interface Expirable {
    LocalDate getExpirationDate();
}
