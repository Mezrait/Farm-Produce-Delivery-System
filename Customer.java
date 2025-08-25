package com.example.assignemnt2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Customer extends User {
    private String deliveryAddress;
    private final List<Product> shoppingCart;
    private final List<Subscription> subscriptions;

    public Customer(String userId, String name, String email, Location location, String deliveryAddress) {
        super(userId, name, email, location);
        this.deliveryAddress = deliveryAddress;
        this.shoppingCart = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void addToCart(Product p) {
        if (p == null) {
            throw new NullPointerException("Product cannot be null");
        }
        shoppingCart.add(p);
    }

    public void removeFromCart(String productId) throws NoSuchElementException {
        boolean found = false;
        for (Product p : shoppingCart) {
            if (p.getProductId().equals(productId)) {
                shoppingCart.remove(p);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NoSuchElementException("Product not found in cart");
        }
    }

    public void clearCart() {
        shoppingCart.clear();
    }

    public List<Product> getShoppingCart() {
        return shoppingCart;
    }

    @Override
    public boolean authenticate(String password) {
        if (password == null || password.isEmpty()) {
            throw new NullPointerException("Password cannot be null");
        }
        return true;
    }

    // No longer needed for checkout, but you can keep it for other services
    @Override
    public boolean isEligibleForService(String location) throws IllegalArgumentException {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Invalid location!");
        }
        return super.isEligibleForService(location);
    }

    // ---- UPDATED: delivery eligibility is based on distance from each product to delivery address ----
    public void checkout() throws NoSuchElementException, OutOfSeasonException, DeliveryUnavailableException {
        if (shoppingCart.isEmpty()) {
            throw new NoSuchElementException("Your shopping cart is empty!");
        }
        for (Product p : shoppingCart) {
            if (!p.isSeasonal(LocalDate.now())) {
                throw new OutOfSeasonException("The product " + p.getName() + " is not in season");
            }
        }

        // Parse the delivery address as a Location (must be latitude,longitude)
        Location deliveryLoc;
        try {
            deliveryLoc = Location.fromString(deliveryAddress);
        } catch (IllegalArgumentException ex) {
            throw new DeliveryUnavailableException("Invalid delivery address: " + ex.getMessage());
        }

        // Check that ALL products are within 50km of the delivery address
        for (Product p : shoppingCart) {
            if (p.getLocation() == null) {
                throw new DeliveryUnavailableException("Product " + p.getName() + " does not have a location set.");
            }
            // Debug print
            System.out.println("DEBUG: Delivery address: " + deliveryAddress);
            System.out.println("DEBUG: Product location: " + p.getLocation().getLatitude() + "," + p.getLocation().getLongitude());
            double distance = p.getLocation().calculateDistance(deliveryLoc);
            System.out.println("DEBUG: Calculated distance = " + distance + " km");
            if (distance > 50) {
                throw new DeliveryUnavailableException(
                        "Product " + p.getName() + " is too far from the delivery address (" +
                                String.format("%.2f", distance) + " km)."
                );
            }
        }


        clearCart();
        System.out.println("Checkout successful");
    }

    public void addSubscription(Subscription subscription) {
        if (subscription == null) {
            throw new IllegalArgumentException("Subscription cannot be null");
        }
        subscriptions.add(subscription);
    }

    public void removeSubscription(String subscriptionId) {
        for (int i = 0; i < subscriptions.size(); i++) {
            if (subscriptions.get(i).getSubscriptionId().equals(subscriptionId)) {
                subscriptions.remove(i);
                break;
            }
        }
    }

    public List<Subscription> getActiveSubscriptions() {
        List<Subscription> activeSubscriptions = new ArrayList<>();
        for (Subscription s : subscriptions) {
            if (s.isActive()) {
                activeSubscriptions.add(s);
            }
        }
        return activeSubscriptions;
    }
}
