package com.example.assignemnt2;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchEngine implements IProductSearch {
    private final List<Product> products;

    public ProductSearchEngine(List<Product> products) {

        this.products = products;
    }

    /**
     * Search for products that are in season at the given date.
     * date - the date to check seasonality for
     * return a list of products that are in season
     */
    @Override
    public List<Product> searchBySeason(LocalDate date){
        List<Product> seasonalProduct = new ArrayList<>();
        for(Product product: products){
            if(product.isSeasonal(date)){
                seasonalProduct.add(product);

            }
        }
        return seasonalProduct;
    }
    /**
     * Search for products that are within a given distance (radius) from the customer.
     * radius - the max distance (e.g., in km)
     * a list of products sorted by distance (closest first)
     */
    @Override
    public List<Product> searchByProximity(int radius) {
        List<Product> nearbyProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getDistanceFromCustomer() <= radius) {
                nearbyProducts.add(product);
            }
        }
        // Basic sorting by distance
        for (int i = 0; i < nearbyProducts.size(); i++) {
            for (int j = i + 1; j < nearbyProducts.size(); j++) {
                if (nearbyProducts.get(i).getDistanceFromCustomer() > nearbyProducts.get(j).getDistanceFromCustomer()) {
                    Product temp = nearbyProducts.get(i);
                    nearbyProducts.set(i, nearbyProducts.get(j));
                    nearbyProducts.set(j, temp);
                }
            }
        }
        return nearbyProducts;
    }


    @Override
    public List<Product> searchByCategory(String category) {
        List<Product> categoryProducts = new ArrayList<>();
        String searchCategory = category.toUpperCase();
        for (Product product : products) {
            if (product.getCategory().equals(searchCategory)) {
                categoryProducts.add(product);
            }
        }
        return categoryProducts;
    }

}
