package com.example.assignemnt2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;

public class CustomerPortal {
    private final Customer customer;
    private final ProductSearchEngine searchEngine;
    private final DeliverySystem deliverySystem;
    private ObservableList<Product> allProducts;
    public static boolean deliveryInProgress = false;

    public CustomerPortal(Customer customer, ProductSearchEngine searchEngine, DeliverySystem ignored) {
        this.customer = customer;
        this.searchEngine = searchEngine;
        this.deliverySystem = MainApp.deliverySystem;

        try {
            List<Product> loadedProducts = FileManager.readProductsFromFile("src/main/java/com/example/Assignemnt2/data/produce_updated.txt");
            this.allProducts = FXCollections.observableArrayList(loadedProducts);
        } catch (Exception e) {
            this.allProducts = FXCollections.observableArrayList();
        }
    }

    public ScrollPane getView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(36, 44, 36, 44));
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #4cb8c4 0%, #3cd3ad 100%);"
                        + "-fx-background-size: cover;"
                        + "-fx-background-position: center center;"
                        + "-fx-background-repeat: no-repeat;"
        );

        Label title = new Label("Welcome, " + customer.getName());
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #212121; -fx-padding: 0 0 14px 0;");

        // Address box
        HBox addressBox = new HBox(10);
        addressBox.setPadding(new Insets(8, 0, 8, 0));
        Label addressLabel = new Label("Delivery Address:");
        addressLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #212121; -fx-font-weight: bold;");
        TextField addressField = new TextField(customer.getDeliveryAddress());
        styleUniformField(addressField);

        Button saveAddressBtn = new Button("Save Address");
        styleBlueButton(saveAddressBtn);
        saveAddressBtn.setPrefWidth(140);
        saveAddressBtn.setPrefHeight(44);

        saveAddressBtn.setOnAction(e -> {
            customer.setDeliveryAddress(addressField.getText());
            showAlert("Success", "Delivery address updated");
        });
        addressBox.getChildren().addAll(addressLabel, addressField, saveAddressBtn);

        // Controls for search/filter (uniform size)
        double inputWidth = 180;
        double inputHeight = 48;

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("VEGETABLES", "FRUITS", "DAIRY", "MEAT", "GRAINS");
        styleUniformField(categoryCombo);
        categoryCombo.setPrefWidth(inputWidth);
        categoryCombo.setPrefHeight(inputHeight);

        Button searchBtn = new Button("Search by Category");
        styleBlueButton(searchBtn);
        searchBtn.setPrefWidth(inputWidth);
        searchBtn.setPrefHeight(inputHeight);

        DatePicker seasonDatePicker = new DatePicker(LocalDate.now());
        styleUniformField(seasonDatePicker);
        seasonDatePicker.setPrefWidth(inputWidth);
        seasonDatePicker.setPrefHeight(inputHeight);

        Button seasonSearchBtn = new Button("Search by Season");
        styleYellowButton(seasonSearchBtn);
        seasonSearchBtn.setPrefWidth(inputWidth);
        seasonSearchBtn.setPrefHeight(inputHeight);

        TextField proximityField = new TextField();
        proximityField.setPromptText("Max distance (km)");
        styleUniformField(proximityField);
        proximityField.setPrefWidth(inputWidth);
        proximityField.setPrefHeight(inputHeight);

        Button proximitySearchBtn = new Button("Search by Proximity");
        styleYellowButton(proximitySearchBtn);
        proximitySearchBtn.setPrefWidth(inputWidth);
        proximitySearchBtn.setPrefHeight(inputHeight);

        FlowPane productsPane = new FlowPane();
        productsPane.setHgap(24);
        productsPane.setVgap(24);
        productsPane.setPadding(new Insets(8));
        productsPane.setStyle("-fx-background-color: transparent;");

        FlowPane cartPane = new FlowPane();
        cartPane.setHgap(16);
        cartPane.setVgap(16);
        cartPane.setPadding(new Insets(8));
        cartPane.setStyle("-fx-background-color: transparent;");

        updateProductCards(productsPane, allProducts, cartPane, addressField);
        updateCartCards(cartPane);

        // Category Search
        searchBtn.setOnAction(e -> {
            String category = categoryCombo.getValue();
            if (category != null) {
                ObservableList<Product> filtered = FXCollections.observableArrayList();
                for (Product p : allProducts) {
                    if (p.getCategory().equalsIgnoreCase(category)) {
                        filtered.add(p);
                    }
                }
                updateProductCards(productsPane, filtered, cartPane, addressField);
            } else {
                updateProductCards(productsPane, allProducts, cartPane, addressField);
            }
        });

        // Seasonal Search
        seasonSearchBtn.setOnAction(e -> {
            LocalDate selectedDate = seasonDatePicker.getValue();
            if (selectedDate != null) {
                ObservableList<Product> seasonal = FXCollections.observableArrayList();
                for (Product p : allProducts) {
                    if (p.isSeasonal(selectedDate)) {
                        seasonal.add(p);
                    }
                }
                updateProductCards(productsPane, seasonal, cartPane, addressField);
            } else {
                showAlert("Error", "Please select a date for seasonal search.");
            }
        });

        // Proximity Search
        proximitySearchBtn.setOnAction(e -> {
            String maxDistStr = proximityField.getText().trim();
            if (maxDistStr.isEmpty()) {
                showAlert("Error", "Enter a maximum distance (km).");
                return;
            }
            double maxDistance;
            try {
                maxDistance = Double.parseDouble(maxDistStr);
                if (maxDistance < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid number for distance.");
                return;
            }
            Location deliveryLoc;
            try {
                deliveryLoc = Location.fromString(addressField.getText());
            } catch (Exception ex) {
                showAlert("Error", "Your delivery address must be in 'lat,long' format for proximity search.");
                return;
            }

            ObservableList<Product> filtered = FXCollections.observableArrayList();
            for (Product p : allProducts) {
                Location prodLoc = p.getLocation();
                if (prodLoc != null) {
                    double distance = prodLoc.calculateDistance(deliveryLoc);
                    if (distance <= maxDistance) {
                        filtered.add(p);
                    }
                }
            }
            updateProductCards(productsPane, filtered, cartPane, addressField);
        });

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle(
                "-fx-background-color: #388e3c; -fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 24px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #8cc88a, 6, 0.10, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        );
        checkoutBtn.setPrefWidth(140);
        checkoutBtn.setPrefHeight(44);
        checkoutBtn.setOnMouseEntered(e -> checkoutBtn.setStyle(
                "-fx-background-color: #246d28; -fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 24px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #8cc88a, 6, 0.10, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        ));
        checkoutBtn.setOnMouseExited(e -> checkoutBtn.setStyle(
                "-fx-background-color: #388e3c; -fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 24px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #8cc88a, 6, 0.10, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        ));
        checkoutBtn.setOnAction(e -> {
            try {
                String deliveryAddr = addressField.getText();
                Location deliveryLoc = Location.fromString(deliveryAddr);

                // 1. Load current products from file
                List<Product> fileProducts = FileManager.readProductsFromFile("src/main/java/com/example/Assignemnt2/data/produce_updated.txt");

                // 2. Reduce quantity for each item in cart
                for (Product cartProduct : customer.getShoppingCart()) {
                    for (Product fileProduct : fileProducts) {
                        // Match by product ID
                        if (fileProduct.getProductId().equals(cartProduct.getProductId())) {
                            int newQty = fileProduct.getQuantityAvailable() - 1; // Assuming only 1 is bought at a time
                            fileProduct.setQuantityAvailable(Math.max(0, newQty));
                        }
                    }
                }

                // 3. Save the updated products list back to file
                FileManager.saveProductsToFile(fileProducts, "src/main/java/com/example/Assignemnt2/data/produce_updated.txt");

                // 4. Checkout and process order
                customer.checkout();
                deliverySystem.processOrder(customer);
                updateCartCards(cartPane);

                // 5. Update product cards as well (so customer sees changes immediately)
                allProducts = FXCollections.observableArrayList(fileProducts);
                updateProductCards(productsPane, allProducts, cartPane, addressField);

                MainApp.deliveryInProgress = true;
                showAlert("Success", "Order processed successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", ex.getMessage());
            }
        });


        // --- Layout assembly
        Label allProductsLabel = new Label("All Products");
        allProductsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #f9f9f9; -fx-font-weight: bold; -fx-padding: 0 0 6px 0;");

        HBox searchRow = new HBox(18, categoryCombo, searchBtn,
                new Label("Seasonal Search:"), seasonDatePicker, seasonSearchBtn,
                new Label("Proximity:"), proximityField, proximitySearchBtn);
        searchRow.setPadding(new Insets(0, 0, 12, 0));
        searchRow.setStyle("-fx-alignment: center-left;");

        Label cartLabel = new Label("Your Shopping Cart");
        cartLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f9f9f9; -fx-font-weight: bold; -fx-padding: 0 0 6px 0;");

        layout.getChildren().addAll(
                title,
                addressBox,
                new Separator(),
                allProductsLabel,
                searchRow,
                productsPane,
                new Separator(),
                cartLabel,
                cartPane,
                checkoutBtn
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    // --- Helper for field style ---
    private void styleUniformField(Control field) {
        field.setStyle(
                "-fx-background-radius: 10px; -fx-background-color: #fff; " +
                        "-fx-border-color: #a0c0d0; -fx-border-radius: 10px; -fx-border-width: 1.5px;" +
                        "-fx-font-size: 18px; -fx-text-fill: #343434;"
        );
    }

    // Style helper for blue-green login-style buttons
    private void styleBlueButton(Button button) {
        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #4cb8c4, #3cd3ad);" +
                        "-fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #82b3c9, 6, 0.1, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: linear-gradient(to right, #3cd3ad, #4cb8c4);" +
                        "-fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #82b3c9, 6, 0.1, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: linear-gradient(to right, #4cb8c4, #3cd3ad);" +
                        "-fx-text-fill: #fff; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, #82b3c9, 6, 0.1, 0, 2); -fx-border-width: 0; -fx-min-width: 140px;"
        ));
    }

    // Style helper for yellow search buttons
    private void styleYellowButton(Button button) {
        button.setStyle(
                "-fx-background-color: #fff9c4; -fx-text-fill: #01857a; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-border-width: 0; -fx-min-width: 140px;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #ffe47a; -fx-text-fill: #01857a; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-border-width: 0; -fx-min-width: 140px;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #fff9c4; -fx-text-fill: #01857a; -fx-background-radius: 12px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px 0; -fx-cursor: hand; -fx-border-width: 0; -fx-min-width: 140px;"
        ));
    }

    // --- Product Cards ---
    private void updateProductCards(FlowPane pane, ObservableList<Product> products, FlowPane cartPane, TextField addressField) {
        pane.getChildren().clear();
        for (Product product : products) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setPrefWidth(240);
            card.setStyle(
                    "-fx-background-color: #fff;" +
                            "-fx-background-radius: 18px;" +
                            "-fx-border-color: #e0f7fa;" +
                            "-fx-border-radius: 18px;" +
                            "-fx-effect: dropshadow(gaussian, #aadbe8, 2, 0.08, 0, 1);"
            );

            ImageView imageView = new ImageView();
            File imgFile = new File(product.getImagePath());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString(), 110, 110, true, true));
            }
            imageView.setFitWidth(110);
            imageView.setFitHeight(110);
            imageView.setPreserveRatio(true);

            Label name = new Label(product.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #212121;");
            Label price = new Label("Price: " + product.getPrice());
            price.setStyle("-fx-font-size: 14px; -fx-text-fill: #222;");
            Label category = new Label("Category: " + product.getCategory());
            category.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
            Label location = new Label("Location: " + product.getLocation());
            location.setStyle("-fx-font-size: 13px; -fx-text-fill: #156a6e;");

            Button addToCartBtn = new Button("Add to Cart");
            addToCartBtn.setStyle("-fx-background-color: #23d3ab; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 7px 14px;");
            addToCartBtn.setOnMouseEntered(e -> addToCartBtn.setStyle("-fx-background-color: #32e5bb; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 7px 14px;"));
            addToCartBtn.setOnMouseExited(e -> addToCartBtn.setStyle("-fx-background-color: #23d3ab; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 7px 14px;"));
            addToCartBtn.setOnAction(e -> {
                if (product.isSeasonal(LocalDate.now())) {
                    customer.addToCart(product);
                    updateCartCards(cartPane);
                } else {
                    showAlert("Error", "This product is not seasonal");
                }
            });

            card.getChildren().addAll(imageView, name, price, category, location, addToCartBtn);
            pane.getChildren().add(card);
        }
    }

    // --- Cart Cards ---
    private void updateCartCards(FlowPane pane) {
        pane.getChildren().clear();
        for (Product product : customer.getShoppingCart()) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setPrefWidth(200);
            card.setStyle(
                    "-fx-background-color: #fffbe9;" +
                            "-fx-background-radius: 16px;" +
                            "-fx-border-color: #ffe0b2;" +
                            "-fx-border-radius: 16px;" +
                            "-fx-effect: dropshadow(gaussian, #ffe082, 2, 0.08, 0, 1);"
            );

            ImageView imageView = new ImageView();
            File imgFile = new File(product.getImagePath());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString(), 90, 90, true, true));
            }
            imageView.setFitWidth(90);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);

            Label name = new Label(product.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #222;");
            Label price = new Label("Price: " + product.getPrice());
            price.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
            Label category = new Label("Category: " + product.getCategory());
            category.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
            Label location = new Label("Location: " + product.getLocation());
            location.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

            Button removeBtn = new Button("Remove");
            removeBtn.setStyle("-fx-background-color: #fc6767; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 7px 10px;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: #d63d2d; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 7px 10px;"));
            removeBtn.setOnMouseExited(e -> removeBtn.setStyle("-fx-background-color: #fc6767; -fx-text-fill: #fff; -fx-background-radius: 7px; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 7px 10px;"));
            removeBtn.setOnAction(e -> {
                customer.removeFromCart(product.getProductId());
                updateCartCards(pane);
            });

            card.getChildren().addAll(imageView, name, price, category, location, removeBtn);
            pane.getChildren().add(card);
        }
    }

    // --- Alert Helper ---
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
