package com.example.assignemnt2;
public abstract class User {
    private String userId;
    private String name;
    private String email;
    private Location location;
    private String notificationPreference;

    private void validateUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID can't be empty!");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '_') {
                throw new IllegalArgumentException("Name must only contain letters, spaces, or underscores");
            }
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }


    public User(String userId, String name, String email, Location location) {
        validateUserId(userId);
        validateName(name);
        validateEmail(email);
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.location = location;
        this.notificationPreference = "EMAIL"; // Default value as String
    }

    public abstract boolean authenticate(String password) throws NullPointerException;

    public boolean isEligibleForService(String locationStr) throws IllegalArgumentException {
        if (locationStr == null || locationStr.isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        // Extra check for comma
        if (!locationStr.matches("^-?\\d{1,2}\\.\\d+,-?\\d{1,3}\\.\\d+$")) {
            throw new IllegalArgumentException("Location must be in 'latitude,longitude' format, e.g., 24.4539,54.3773");
        }
        Location targetLocation = Location.fromString(locationStr);
        return this.location.calculateDistance(targetLocation) <= 50;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID can't be empty!");
        }
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '_') {
                throw new IllegalArgumentException("Name must only contain letters, spaces, or underscores");
            }
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) throws IllegalArgumentException, NullPointerException {
        this.location = location;
    }

    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String preference) {
        if (preference == null || preference.isEmpty()) {
            throw new IllegalArgumentException("Notification preference cannot be null or empty");
        }
        this.notificationPreference = preference;
    }
}