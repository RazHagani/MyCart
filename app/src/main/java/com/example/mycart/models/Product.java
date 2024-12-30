package com.example.mycart.models;

public class Product {
    private String name;
    private int quantity;

    // קונסטרקטור ריק נדרש על ידי Firebase
    public Product() {}

    // קונסטרקטור מותאם אישית
    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    // Getters ו-Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
