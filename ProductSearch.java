package com.example.assignemnt2;
import java.time.LocalDate;
import java.util.List;

public class ProductSearch implements IProductSearch {
    @Override
    public List<Product> searchBySeason(LocalDate date) {
        // Implementation moved to ProductSearchEngine
        throw new UnsupportedOperationException("Use ProductSearchEngine instead");
    }

    @Override
    public List<Product> searchByProximity(int radius) {
        throw new UnsupportedOperationException("Use ProductSearchEngine instead");
    }

    @Override
    public List<Product> searchByCategory(String category) {
        throw new UnsupportedOperationException("Use ProductSearchEngine instead");
    }
}
