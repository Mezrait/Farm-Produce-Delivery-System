package com.example.assignemnt2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static DeliverySystem deliverySystem;
    private BorderPane root;
    private Customer currentCustomer;
    private Farmer currentFarmer;
    private ProductSearchEngine productSearchEngine;
    public static boolean deliveryInProgress = false;

    @Override
    public void start(Stage primaryStage) {
        // Show login dialog first
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLogin((userType, userObject) -> {
            if ("Customer".equals(userType)) {
                this.currentCustomer = (Customer) userObject;
                this.currentFarmer = null;
            } else if ("Farmer".equals(userType)) {
                this.currentFarmer = (Farmer) userObject;
                this.currentCustomer = null;
            }
            initializeSystemAfterLogin();
            setupMainWindow(primaryStage, userType);
        });
        loginScreen.start(new Stage());
    }

    private void initializeSystemAfterLogin() {
        if (MainApp.deliverySystem == null) {
            MainApp.deliverySystem = new DeliverySystem();
        }
        if (currentCustomer != null)
            MainApp.deliverySystem.addCustomer(currentCustomer);
        if (currentFarmer != null)
            MainApp.deliverySystem.addFarmer(currentFarmer);

        if (currentFarmer != null && currentFarmer.getProductList() != null) {
            this.productSearchEngine = new ProductSearchEngine(currentFarmer.getProductList());
        } else if (currentCustomer != null && currentCustomer.getShoppingCart() != null) {
            this.productSearchEngine = new ProductSearchEngine(currentCustomer.getShoppingCart());
        }
    }

    private void setupMainWindow(Stage primaryStage, String userType) {
        root = new BorderPane();

        // --- Navigation Bar ---
        HBox navBar = new HBox(18);
        navBar.setPadding(new Insets(12, 32, 12, 32));
        navBar.setAlignment(Pos.CENTER);
        navBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #4cb8c4, #3cd3ad);" +
                        "-fx-background-radius: 0 0 22px 22px;" +
                        "-fx-effect: dropshadow(gaussian, #aadbe8, 6, 0.12, 0, 2);"
        );

        // --- Title (centered) ---
        Label logo = new Label("🌱 Farm Delivery System");
        logo.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #135c57;" +
                        "-fx-padding: 0 40px 0 0;"
        );

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // --- Buttons ---
        Button customerBtn = new Button("Customer Portal");
        Button farmerBtn = new Button("Farmer Dashboard");
        Button deliveryBtn = new Button("Delivery Tracking");
        Button logoutBtn = new Button("Logout");

        String btnStyle =
                "-fx-background-color: linear-gradient(to right, #ffffff 70%, #cff8f4 100%);" +
                        "-fx-text-fill: #16b7aa;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 8px 20px;" +
                        "-fx-effect: dropshadow(three-pass-box, #aee0e5, 1.7, 0.07, 0, 2);" +
                        "-fx-cursor: hand; " +
                        "-fx-border-width: 0;";

        customerBtn.setStyle(btnStyle);
        farmerBtn.setStyle(btnStyle);
        deliveryBtn.setStyle(btnStyle);
        logoutBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #fc6767, #ec008c);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 8px 22px;" +
                        "-fx-effect: dropshadow(three-pass-box, #f7bccd, 1.2, 0.07, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0;"
        );

        // Button hover (optional for logout, add if you want)
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #ec008c, #fc6767);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 8px 22px;" +
                        "-fx-effect: dropshadow(three-pass-box, #f7bccd, 1.7, 0.10, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #fc6767, #ec008c);" +
                        "-fx-text-fill: #fff;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-padding: 8px 22px;" +
                        "-fx-effect: dropshadow(three-pass-box, #f7bccd, 1.2, 0.07, 0, 2);" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0;"
        ));

        // Button actions
        customerBtn.setOnAction(e -> showCustomerPortal());
        farmerBtn.setOnAction(e -> showFarmerDashboard());
        deliveryBtn.setOnAction(e -> showDeliveryMap());
        logoutBtn.setOnAction(e -> {
            primaryStage.close();
            start(new Stage());
        });

        // Set navigation bar according to user type
        if ("Customer".equals(userType)) {
            navBar.getChildren().addAll(leftSpacer, logo, customerBtn, deliveryBtn, rightSpacer, logoutBtn);
        } else if ("Farmer".equals(userType)) {
            navBar.getChildren().addAll(leftSpacer, logo, farmerBtn, rightSpacer, logoutBtn);
        } else {
            navBar.getChildren().addAll(leftSpacer, logo, rightSpacer, logoutBtn);
        }

        root.setTop(navBar);

        // Show default view based on login
        if ("Customer".equals(userType)) {
            showCustomerPortal();
        } else if ("Farmer".equals(userType)) {
            showFarmerDashboard();
        }

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Farm Delivery System");
        primaryStage.setWidth(1500);
        primaryStage.setHeight(800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCustomerPortal() {
        if (currentCustomer == null) return;
        CustomerPortal portal = new CustomerPortal(currentCustomer, productSearchEngine, deliverySystem);
        root.setCenter(portal.getView());
    }

    private void showFarmerDashboard() {
        if (currentFarmer == null) return;
        FarmerDashboard dashboard = new FarmerDashboard(currentFarmer);
        root.setCenter(dashboard.getView());
    }

    private void showDeliveryMap() {
        DeliveryMap map = new DeliveryMap(currentCustomer, currentFarmer);
        root.setCenter(map.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
