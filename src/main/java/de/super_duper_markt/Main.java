package de.super_duper_markt;

import de.super_duper_markt.storage.StorageManager;

public class Main {
    public static void main(String[] args) {
        StorageManager manager = StorageManager.getStorageManagerInstance();
        manager.initiateStorageManagement();
    }
}