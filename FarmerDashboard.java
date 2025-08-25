package com.example.assignemnt2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class FarmerDashboard {
    private final Farmer farmer;
    private ObservableList<Product> products;
    private static final String PRODUCTS_FILE = "src/main/java/com/example/Assignemnt2/data/produce_updated.txt";
    private String selectedImagePath = "";

    // Track selected product for removal (class field)
    private Product[] selectedProductReference = {null};

    public FarmerDashboard(Farmer farmer) {
        this.farmer = farmer;
        try {
            var loadedProducts = FileManager.readProductsFromFile(PRODUCTS_FILE);
            System.out.println("Farmer loaded products: " + loadedProducts.size());
            farmer.setProductList(loadedProducts);
            this.products = FXCollections.observableArrayList(loadedProducts);
        } catch (Exception e) {
            e.printStackTrace();
            this.products = FXCollections.observableArrayList();
        }
    }

    public ScrollPane getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(12));
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #4cb8c4 0%, #3cd3ad 100%);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-effect: dropshadow(gaussian, #a5dbcc, 9, 0.14, 0, 2);"
        );

        Label title = new Label("Farmer Dashboard - " + farmer.getName());
        title.setStyle(
                "-fx-font-size: 19px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #264e4a;" +
                        "-fx-padding: 0 0 2px 0;" +
                        "-fx-effect: dropshadow(gaussian, #e7f9f9, 1.2, 0.2, 0, 0);"
        );

        // Address bar
        TextField addressField = new TextField(farmer.getFarmAddress());
        addressField.setStyle(
                "-fx-background-radius: 7px;" +
                        "-fx-background-color: #fff;" +
                        "-fx-border-color: #b7e2d6;" +
                        "-fx-border-radius: 7px;" +
                        "-fx-border-width: 1px;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 6px 9px;" +
                        "-fx-max-width: 230px;" +
                        "-fx-text-fill: #222;"
        );

        Button saveAddressBtn = new Button("Save Address");
        saveAddressBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #4cb8c4, #3cd3ad);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, #aee0e5, 3, 0.10, 0, 1);" +
                        "-fx-border-width: 0;"
        );

        HBox addressBar = new HBox(6, new Label("Farm Address:"), addressField, saveAddressBtn);
        addressBar.setStyle("-fx-spacing: 6px; -fx-alignment: center-left; -fx-padding: 0 0 5px 0;");

        saveAddressBtn.setOnAction(e -> {
            farmer.setFarmAddress(addressField.getText());
            showAlert("Success", "Farm address updated");
        });

        Label productsLabel = new Label("Your Products");
        productsLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #24706c; -fx-padding: 2px 0 2px 0;");

        // --- Product Card Grid Instead of TableView ---
        FlowPane productsPane = new FlowPane();
        productsPane.setHgap(16);
        productsPane.setVgap(16);
        productsPane.setPadding(new Insets(8));
        productsPane.setStyle("-fx-background-color: transparent;");

        // -- Remove Product Button --
        Button removeProductBtn = new Button("Remove Selected");
        removeProductBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #46cab1, #47e6a6);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, #aee0e5, 3, 0.10, 0, 1);" +
                        "-fx-border-width: 0;" +
                        "-fx-min-width: 140px;"
        );

        removeProductBtn.setOnAction(e -> {
            if (selectedProductReference[0] != null) {
                products.remove(selectedProductReference[0]);
                FileManager.saveProductsToFile(products, PRODUCTS_FILE);
                ArrayList<Product> reloaded = null;
                try {
                    reloaded = FileManager.readProductsFromFile(PRODUCTS_FILE);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                products.setAll(reloaded);
                updateProductsPane(productsPane);
                selectedProductReference[0] = null;
            } else {
                showAlert("No Selection", "Please select a product card to remove.");
            }
        });

        // --- Add Product Form ---
        Label addProductLabel = new Label("Add New Product");
        addProductLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #24706c; -fx-padding: 2px 0 2px 0;");

        GridPane productForm = new GridPane();
        productForm.setHgap(7);
        productForm.setVgap(7);
        productForm.setStyle("-fx-hgap: 7px; -fx-vgap: 7px; -fx-padding: 6px 0 6px 0;");

        TextField nameField = new TextField();
        nameField.setStyle(addressField.getStyle());
        TextField priceField = new TextField();
        priceField.setStyle(addressField.getStyle());
        TextField quantityField = new TextField();
        quantityField.setStyle(addressField.getStyle());
        DatePicker harvestDatePicker = new DatePicker();
        harvestDatePicker.setStyle(addressField.getStyle());

        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setStyle(addressField.getStyle());
        categoryCombo.getItems().addAll("VEGETABLES", "FRUITS", "DAIRY", "MEAT", "GRAINS");

        TextField locationField = new TextField();
        locationField.setPromptText("e.g., 24.4539,54.3773");
        locationField.setStyle(addressField.getStyle());

        Button uploadBtn = new Button("Upload Photo");
        uploadBtn.setStyle(saveAddressBtn.getStyle());
        Label photoLabel = new Label("(No file)");
        photoLabel.setStyle("-fx-text-fill: #3cd3ad; -fx-font-size: 13px;");

        // Accept all popular image file types
        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.tif", "*.tiff", "*.webp", "*.svg"
                    ),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                selectedImagePath = file.getAbsolutePath();
                photoLabel.setText(file.getName());
            }
        });

        productForm.add(new Label("Name:"), 0, 0);
        productForm.add(nameField, 1, 0);
        productForm.add(new Label("Price:"), 0, 1);
        productForm.add(priceField, 1, 1);
        productForm.add(new Label("Quantity:"), 0, 2);
        productForm.add(quantityField, 1, 2);
        productForm.add(new Label("Harvest Date:"), 0, 3);
        productForm.add(harvestDatePicker, 1, 3);
        productForm.add(new Label("Category:"), 0, 4);
        productForm.add(categoryCombo, 1, 4);
        productForm.add(new Label("Photo:"), 0, 5);
        productForm.add(new HBox(8, uploadBtn, photoLabel), 1, 5);
        productForm.add(new Label("Location (lat,long):"), 0, 6);
        productForm.add(locationField, 1, 6);

        Button addProductBtn = new Button("Add Product");
        addProductBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #34d2bb, #63e7c5);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, #aee0e5, 3, 0.10, 0, 1);" +
                        "-fx-border-width: 0;" +
                        "-fx-min-width: 140px;"
        );
        addProductBtn.setOnAction(e -> {
            try {
                String locationStr = locationField.getText().trim();
                if (nameField.getText().isEmpty() ||
                        priceField.getText().isEmpty() ||
                        quantityField.getText().isEmpty() ||
                        harvestDatePicker.getValue() == null ||
                        categoryCombo.getValue() == null ||
                        locationStr.isEmpty()) {
                    showAlert("Error", "Please fill in all fields, including location.");
                    return;
                }
                try {
                    Location location = Location.fromString(locationStr);
                } catch (Exception locEx) {
                    showAlert("Error", "Invalid latitude/longitude format. Example: 24.4539,54.3773");
                    return;
                }
                ObservableList<Product> latestProducts;
                try {
                    latestProducts = FXCollections.observableArrayList(
                            FileManager.readProductsFromFile(PRODUCTS_FILE)
                    );
                } catch (Exception reloadEx) {
                    showAlert("Error", "Failed to reload products from file.");
                    return;
                }

                Product product = new Product(
                        "P" + System.currentTimeMillis(),
                        nameField.getText(),
                        "No_description",
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(quantityField.getText()),
                        harvestDatePicker.getValue(),
                        categoryCombo.getValue(),
                        0,
                        selectedImagePath,
                        locationStr
                );
                latestProducts.add(product);

                FileManager.saveProductsToFile(latestProducts, PRODUCTS_FILE);

                var reloaded = FileManager.readProductsFromFile(PRODUCTS_FILE);
                products.setAll(reloaded);

                // update product cards UI
                updateProductsPane(productsPane);

                clearFields(nameField, priceField, quantityField, locationField);
                harvestDatePicker.setValue(null);
                categoryCombo.setValue(null);
                selectedImagePath = "";
                photoLabel.setText("(No file)");
                showAlert("Success", "Product added successfully");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        // Initial product cards
        updateProductsPane(productsPane);

        layout.getChildren().addAll(
                title,
                addressBar,
                new Separator(),
                productsLabel,
                productsPane,
                removeProductBtn,
                new Separator(),
                addProductLabel,
                productForm,
                addProductBtn
        );

        // Wrap the VBox in a ScrollPane for usability
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    // Update product cards UI with selection logic
    private void updateProductsPane(FlowPane productsPane) {
        productsPane.getChildren().clear();

        for (Product product : products) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(8));
            card.setPrefWidth(180);
            card.setStyle(
                    "-fx-background-color: #ffffffee;" +
                            "-fx-background-radius: 12px;" +
                            "-fx-border-color: #a0c0d0;" +
                            "-fx-border-radius: 12px;" +
                            "-fx-effect: dropshadow(gaussian, #c2f3e0, 2, 0.08, 0, 1);" +
                            "-fx-cursor: hand;"
            );

            // Product image
            ImageView imageView = new ImageView();
            File imgFile = new File(product.getImagePath());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString(), 110, 110, true, true));
            }
            imageView.setFitWidth(110);
            imageView.setFitHeight(110);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-background-radius: 8px;");

            // Product details
            Label name = new Label(product.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label price = new Label("Price: " + product.getPrice());
            Label quantity = new Label("Qty: " + product.getQuantityAvailable());
            Label category = new Label("Category: " + product.getCategory());
            Label location = new Label("Location: " + product.getLocation());

            card.getChildren().addAll(imageView, name, price, quantity, category, location);

            card.setOnMouseClicked(ev -> {
                // Deselect all cards
                productsPane.getChildren().forEach(node -> node.setStyle(
                        "-fx-background-color: #ffffffee;" +
                                "-fx-background-radius: 12px;" +
                                "-fx-border-color: #a0c0d0;" +
                                "-fx-border-radius: 12px;" +
                                "-fx-effect: dropshadow(gaussian, #c2f3e0, 2, 0.08, 0, 1);" +
                                "-fx-cursor: hand;"
                ));
                // Highlight selected card
                card.setStyle(
                        "-fx-background-color: #e1fcf9;" +
                                "-fx-background-radius: 12px;" +
                                "-fx-border-color: #24aea6;" +
                                "-fx-border-width: 2px;" +
                                "-fx-border-radius: 12px;" +
                                "-fx-effect: dropshadow(gaussian, #c2f3e0, 2, 0.10, 0, 1);" +
                                "-fx-cursor: hand;"
                );
                selectedProductReference[0] = product;
            });

            productsPane.getChildren().add(card);
        }

        // Allow clearing selection by clicking empty FlowPane area
        productsPane.setOnMouseClicked(event -> {
            if (event.getTarget() == productsPane) {
                productsPane.getChildren().forEach(node -> node.setStyle(
                        "-fx-background-color: #ffffffee;" +
                                "-fx-background-radius: 12px;" +
                                "-fx-border-color: #a0c0d0;" +
                                "-fx-border-radius: 12px;" +
                                "-fx-effect: dropshadow(gaussian, #c2f3e0, 2, 0.08, 0, 1);" +
                                "-fx-cursor: hand;"
                ));
                selectedProductReference[0] = null;
            }
        });
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
