package com.example.assignemnt2;

import java.time.LocalDate;

public class Product {
    // Fields
    private final String productId;
    private final String name;
    private final String description;
    private final double price;
    private int quantityAvailable;
    private LocalDate harvestDate;
    private final String category;
    private int distanceFromCustomer;
    private final String imagePath;
    private Location location;

    // Primary constructor (all fields including location)
    public Product(String productId, String name, String description, double price,
                   int quantityAvailable, LocalDate harvestDate, String category,
                   String imagePath, Location location) throws HarvestDateException {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
        setHarvestDate(harvestDate);
        this.category = category;
        this.imagePath = imagePath;
        this.location = location;
    }

    // Secondary constructor for file loading (string category, distance, imagePath, location string)
    public Product(String productId, String name, String description, double price,
                   int quantity, LocalDate harvestDate, String category, int distance,
                   String imagePath, String locationStr) throws HarvestDateException {
        this(productId, name, description, price, quantity, harvestDate,
                category.toUpperCase(), imagePath, Location.fromString(locationStr));
        this.distanceFromCustomer = distance;
    }

    // Minimal constructor for productId only
    public Product(String productId) {
        this.productId = productId;
        this.name = "Unknown";
        this.description = "Not available";
        this.price = 0.0;
        this.quantityAvailable = 0;
        this.harvestDate = LocalDate.now();
        this.category = "VEGETABLES";
        this.imagePath = "";
        this.location = null;
    }

    // Business logic methods
    public boolean isSeasonal(LocalDate date) {
        int currentSeason = (date.getMonthValue() - 1) / 3;
        int harvestSeason = (harvestDate.getMonthValue() - 1) / 3;
        return currentSeason == harvestSeason && date.getYear() == harvestDate.getYear();
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantityAvailable = quantity;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public final void setHarvestDate(LocalDate harvestDate) throws HarvestDateException {
        if (harvestDate.isBefore(LocalDate.now())) {
            throw new HarvestDateException("Harvest date must be in the future");
        }
        this.harvestDate = harvestDate;
    }

    public String getCategory() {
        return category;
    }

    public int getDistanceFromCustomer() {
        return distanceFromCustomer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Override equals to compare by productId
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;             // Same memory reference
        if (obj == null || getClass() != obj.getClass()) return false; // Different class
        Product other = (Product) obj;
        return productId.equals(other.productId); // Same productId means equal
    }

    // Override toString for meaningful product representation
    @Override
    public String toString() {
        return String.format("%s ($%.2f) - %s", name, price, category);
    }
}
