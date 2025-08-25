package com.example.assignemnt2;
public class Location implements ILocation {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude value");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude value");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public static Location fromString(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location string cannot be empty");
        }
        String[] coords = location.split(",");
        if (coords.length != 2) {
            throw new IllegalArgumentException("Location must be in format 'latitude,longitude'");
        }
        try {
            double lat = Double.parseDouble(coords[0].trim());
            double lon = Double.parseDouble(coords[1].trim());
            return new Location(lat, lon);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinates format");
        }
    }

    @Override
    public double calculateDistance(ILocation other) {
        final int EARTH_RADIUS = 6371;
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.getLatitude());
        double dLat = Math.toRadians(other.getLatitude() - this.latitude);
        double dLon = Math.toRadians(other.getLongitude() - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    @Override
    public String toString() {
        return latitude + "," + longitude;
    }

}
