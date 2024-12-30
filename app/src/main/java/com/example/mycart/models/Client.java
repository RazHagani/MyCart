package com.example.mycart.models;

import java.util.ArrayList;

public class Client {
    private String username;
    private String email;
    private String password;
    private String phone;
    private ArrayList<Product> products; // רשימת מוצרים

    // קונסטרקטור ריק (נדרש על ידי Firebase)
    public Client() {}

    // קונסטרקטור מותאם אישית
    public Client(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.products = new ArrayList<>(); // אתחול הרשימה
    }

    // Getters ו-Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    // הוספת מוצר לרשימה
    public void addProduct(Product product) {
        for (Product p : products) {
            if (p.getName().equals(product.getName())) {
                p.setQuantity(p.getQuantity() + product.getQuantity());
                return;
            }
        }
        products.add(product);
    }

    // מחיקת מוצר
    public void deleteProduct(String productName) {
        products.removeIf(p -> p.getName().equals(productName));
    }

    // עדכון כמות מוצר
    public void updateProductQuantity(String productName, int newQuantity) {
        for (Product p : products) {
            if (p.getName().equals(productName)) {
                p.setQuantity(newQuantity);
                return;
            }
        }
    }
}

