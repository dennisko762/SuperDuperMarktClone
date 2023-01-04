package de.super_duper_markt.models.product;

import java.util.Date;

public interface Storable {

    boolean checkIfProductIsAddable();

    boolean checkIfProductIsRemovable();

    void updateQuality(Date currentDate);
}
