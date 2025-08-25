package com.example.assignemnt2;
public interface ILocation {
    double getLatitude();
    double getLongitude();
    double calculateDistance(ILocation other);
}
