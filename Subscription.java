package com.example.assignemnt2;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Subscription {
    private final String subscriptionId;
    private final Customer customer;
    private final SubscriptionType type;
    private final DayOfWeek deliveryDay;
    private final List<Product> weeklyProducts;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public static class SubscriptionType {
        private final String name;
        public SubscriptionType(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    public Subscription(String subscriptionId, Customer customer, SubscriptionType type,
                       DayOfWeek deliveryDay, LocalDate startDate, LocalDate endDate) {
        this.subscriptionId = subscriptionId;
        this.customer = customer;
        this.type = type;
        this.deliveryDay = deliveryDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyProducts = new ArrayList<>();
    }

    // Getters
    public String getSubscriptionId() { return subscriptionId; }
    public Customer getCustomer() { return customer; }
    public SubscriptionType getType() { return type; }
    public DayOfWeek getDeliveryDay() { return deliveryDay; }
    public List<Product> getWeeklyProducts() { return new ArrayList<>(weeklyProducts); }
    
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
}
