package com.example.assignemnt2;
import java.time.LocalDate;
import java.util.List;

public interface IProductSearch {
    List<Product> searchBySeason(LocalDate date);
    List<Product> searchByProximity(int radius);
    List<Product> searchByCategory(String category);
}