package com.example.assignemnt2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliverySystem {
    private final List<Product> inventory;
    private final List<Customer> customers;
    private final List<Farmer> farmers;
    private final List<DeliveryRecord> deliveryRecords; // NEW: Tracks deliveries

    public DeliverySystem() {
        this.inventory = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.farmers = new ArrayList<>();
        this.deliveryRecords = new ArrayList<>(); // Initialize
    }

    /**
     * Processes an order for a given customer.
     * - Copies items from their cart
     * - Updates inventory quantities
     * - Records the delivery in deliveryRecords
     * - Clears the customer's cart
     */
    public void processOrder(Customer customer) throws DeliveryUnavailableException {
////        synchronized (this) {
//            // Make a copy of the customer's cart products (so we don't modify the original list directly)
            List<Product> deliveredProducts = new ArrayList<>(customer.getShoppingCart());
//
//            // For each product in the cart, reduce its inventory quantity by 1
            for (Product product : deliveredProducts) {
                updateInventoryQuantity(product.getProductId(), -1);
//            }

            // If there are products delivered, create a delivery record and save it
            if (!deliveredProducts.isEmpty()) {
                DeliveryRecord record = new DeliveryRecord(
                        customer.getName(),
                        customer.getDeliveryAddress(),
                        deliveredProducts,
                        new Date()
                );
                deliveryRecords.add(record);
            }

            // Clear the customer's cart
            customer.clearCart();
        }
    }


    /**
     * Handles all weekly subscription deliveries for all customers.
     * Loops through all customers and their subscriptions, and delivers products.
     */
    public void handleWeeklyDeliveries() {
        for (Customer customer : customers) {
            for (Subscription subscription : customer.getActiveSubscriptions()) {
                try {
                    processSubscriptionDelivery(subscription);
                } catch (DeliveryUnavailableException e) {
                    System.err.println("Delivery failed: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Internal method to process a subscription delivery.
     * Similar to processOrder, but for weekly subscriptions.
     */
    private void processSubscriptionDelivery(Subscription subscription) throws DeliveryUnavailableException {
            // Check if delivery is available for customer's address
            Customer customer = subscription.getCustomer();
            if (!customer.isEligibleForService(customer.getDeliveryAddress())) {
                throw new DeliveryUnavailableException("Delivery not available for subscription");
            }

            // Copy products from subscription
            List<Product> deliveredProducts = new ArrayList<>(subscription.getWeeklyProducts());

            // Update inventory for each product
            for (Product product : deliveredProducts) {
                updateInventoryQuantity(product.getProductId(), -1);
            }
            // Create a delivery record for this subscription delivery
            if (!deliveredProducts.isEmpty()) {
                DeliveryRecord record = new DeliveryRecord(
                        customer.getName(),
                        customer.getDeliveryAddress(),
                        deliveredProducts,
                        new Date()
                );
                deliveryRecords.add(record);
            }
    }

    public void addCustomer(Customer customer) {
        if (customer != null) {
            customers.add(customer);
        }
    }

    public void addFarmer(Farmer farmer) {
        if (farmer != null) {
            farmers.add(farmer);
        }
    }
    /**
     * Update (or add) a product's inventory to a specific quantity.
     * If the product doesn't exist, it is added.
     */
    public void updateInventory(String productId, int quantity) {
        try {
            Product product = findProductById(productId);
            if (product == null) {
                // If product doesn't exist, create and add it to inventory
                product = new Product(productId);
                product.setQuantityAvailable(quantity);
                inventory.add(product);

            } else {
                // Otherwise, just update the available quantity

                product.setQuantityAvailable(quantity);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
        }
    }


    private void updateInventoryQuantity(String productId, int quantityChange) {
        Product product = findProductById(productId);
        if (product != null) {
            product.setQuantityAvailable(product.getQuantityAvailable() + quantityChange);
        }
    }
    // Helper to find a product in the inventory by its ID.
    private Product findProductById(String productId) {
        for (Product product : inventory) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }


    // NEW: View all delivery records
    public List<DeliveryRecord> getDeliveryRecords() {
        return new ArrayList<>(deliveryRecords);
    }
}
