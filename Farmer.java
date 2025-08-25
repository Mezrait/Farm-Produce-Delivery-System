package com.example.assignemnt2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Farmer extends User {
    private String farmAddress;
    private final List<Product> productList;

    public Farmer(String userId, String name, String email, Location location, String farmAddress) {
        super(userId, name, email, location);
        this.farmAddress = farmAddress;
        this.productList = new ArrayList<>();
    }

    public String getFarmAddress() {
        return farmAddress;
    }

    public void setFarmAddress(String farmAddress) {
        if (farmAddress == null || farmAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Farm address cannot be empty");
        }
        this.farmAddress = farmAddress;
    }

    public List<Product> getProductList() {
        return new ArrayList<>(productList); // Return copy to maintain encapsulation
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (productList.stream().anyMatch(p -> p.getProductId().equals(product.getProductId()))) {
            throw new IllegalArgumentException("Product with ID " + product.getProductId() + " already exists");
        }

        productList.add(product);
    }

    public void removeProduct(String productId) {
        if (!productList.removeIf(p -> p.getProductId().equals(productId))) {
            throw new NoSuchElementException("Product with ID " + productId + " not found");
        }
    }

    public void updateHarvestDate(String productId, LocalDate newDate) throws HarvestDateException {
        if (newDate.isBefore(LocalDate.now())) {
            throw new HarvestDateException("Harvest date cannot be in the past");
        }

        Product product = productList.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        product.setHarvestDate(newDate);
    }

    @Override
    public boolean authenticate(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // Implement actual authentication logic here
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (Farm: %s)", getName(), farmAddress);
    }

    public void setProductList(ArrayList<Product> loadedProducts) {

    }
}