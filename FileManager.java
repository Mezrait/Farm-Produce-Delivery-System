package com.example.assignemnt2;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class FileManager {

    // 1. Read all products from a given file (as Product objects)
    public static ArrayList<Product> readProductsFromFile(String filename) throws Exception {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Data file not found: " + filename +
                    ". Please ensure data files are in the correct location.");
        }
        ArrayList<Product> products = new ArrayList<>();
        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 10) continue; // must have 10 fields

                String productId = parts[0];
                String name = parts[1].replace("_", " ");
                String description = parts[2].replace("_", " ");
                double price = Double.parseDouble(parts[3]);
                int quantity = Integer.parseInt(parts[4]);
                LocalDate harvestDate = LocalDate.parse(parts[5]); // <-- Use this!
                String category = parts[6];
                int distanceFromCustomer = Integer.parseInt(parts[7]);
                String imagePath = parts[8];
                String locationStr = parts[9];

                Product product = new Product(
                        productId,
                        name,
                        description,
                        price,
                        quantity,
                        harvestDate,
                        category,
                        distanceFromCustomer,
                        imagePath,
                        locationStr
                );
                products.add(product);
            }
        }
        return products;
    }


    // 2. Write all products to the given file (overwrites!)
    public static void saveProductsToFile(List<Product> products, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            for (Product product : products) {
                String imagePath = (product.getImagePath() == null || product.getImagePath().trim().isEmpty())
                        ? "none"
                        : product.getImagePath().replace(" ", "_"); // remove spaces from file paths

                String locationStr = "0.0,0.0";
                if (product.getLocation() != null) {
                    // Save as "latitude,longitude"
                    locationStr = product.getLocation().getLatitude() + "," + product.getLocation().getLongitude();
                }

                writer.println(
                        product.getProductId() + " " +
                                product.getName().replace(" ", "_") + " " +
                                product.getDescription().replace(" ", "_") + " " +
                                product.getPrice() + " " +
                                product.getQuantityAvailable() + " " +
                                product.getHarvestDate().toString() + " " + // Use ISO date for easier debugging
                                product.getCategory() + " " +
                                product.getDistanceFromCustomer() + " " +
                                imagePath + " " +
                                locationStr
                );
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }



}
