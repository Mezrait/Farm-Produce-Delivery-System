package com.example.assignemnt2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class DeliveryMap {
    private final Customer customer;
    private final Farmer farmer;

    // Adjust this path as needed!
    private static final String MAP_PATH = "file:/C:/JavaProject/Assignemnt2/src/main/java/com/example/assignemnt2/data/map.png";

    // Animation settings
    private static final int TOTAL_STEPS = 20; // How many steps for the delivery marker to travel
    private static final int STEP_DURATION_SEC = 1; // Each step duration (seconds)

    public DeliveryMap(Customer customer, Farmer farmer) {
        this.customer = customer;
        this.farmer = farmer;
    }

    public Pane getView() {
        VBox layout = new VBox(32);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new javafx.geometry.Insets(32, 0, 32, 0));
        layout.setStyle("-fx-background-color: #f6f7fa;");

        // Title
        Label title = new Label("Delivery Tracking Map (Simulated)");
        title.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #237474;" +
                        "-fx-padding: 10 0 18 0;" +
                        "-fx-effect: dropshadow(gaussian, #e7f9f9, 1.2, 0.2, 0, 0);" +
                        "-fx-alignment: center;"
        );

        // Map size
        double mapWidth = 700;
        double mapHeight = 400;

        // Card to wrap map
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #fff;" +
                        "-fx-background-radius: 22px;" +
                        "-fx-effect: dropshadow(gaussian, #a3e5d8, 9, 0.13, 0, 2);" +
                        "-fx-padding: 20 20 20 20;" +
                        "-fx-min-width: 720px;" +
                        "-fx-min-height: 430px;" +
                        "-fx-max-width: 820px;"
        );

        // Map image
        ImageView mapView = new ImageView();
        mapView.setImage(new Image(MAP_PATH));
        mapView.setFitWidth(mapWidth);
        mapView.setFitHeight(mapHeight);
        mapView.setPreserveRatio(false);
        mapView.setStyle("-fx-background-radius: 20px;");

        Pane mapPane = new Pane(mapView);
        mapPane.setPrefSize(mapWidth, mapHeight);
        mapPane.setStyle("-fx-background-radius: 20px;");

        // Place farm marker (top-left)
        double farmX = 40, farmY = 40;
        Circle farmDot = new Circle(farmX, farmY, 17, Color.web("#10b148"));
        farmDot.setStroke(Color.web("#147134"));
        farmDot.setStrokeWidth(2.3);

        Label farmLabel = new Label("Farm");
        farmLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #156d39;" +
                        "-fx-background-color: #e7fbe2;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 2px 10px 2px 10px;"
        );
        farmLabel.setLayoutX(farmX - 24);
        farmLabel.setLayoutY(farmY - 34);

        // Place customer marker (bottom-right)
        double custX = mapWidth - 40, custY = mapHeight - 40;
        Circle custDot = new Circle(custX, custY, 17, Color.web("#ff9500"));
        custDot.setStroke(Color.web("#d97900"));
        custDot.setStrokeWidth(2.3);

        Label custLabel = new Label("Customer");
        custLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #a36606;" +
                        "-fx-background-color: #fff4c6;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 2px 10px 2px 10px;"
        );
        custLabel.setLayoutX(custX - 43);
        custLabel.setLayoutY(custY + 13);

        // Delivery marker, start at farm
        Circle deliveryMarker = new Circle(farmX, farmY, 12, Color.web("#b91c20"));
        deliveryMarker.setStroke(Color.web("#910f16"));
        deliveryMarker.setStrokeWidth(2);

        // Status label
        Label statusLabel = new Label();
        statusLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: 500;" +
                        "-fx-text-fill: #507573;" +
                        "-fx-padding: 18px 0 0 0;" +
                        "-fx-alignment: center;"
        );
        statusLabel.setLayoutY(mapHeight + 22);

        // Add items to mapPane (background must be first)
        mapPane.getChildren().addAll(farmDot, custDot, farmLabel, custLabel);

        card.getChildren().add(mapPane);

        // Add map, status label to layout
        layout.getChildren().addAll(title, card, statusLabel);

        // -- Only animate if a delivery was just made
        if (MainApp.deliveryInProgress) {
            mapPane.getChildren().add(deliveryMarker); // Only add marker if animating
            animateDelivery(farmX, farmY, custX, custY, deliveryMarker, statusLabel);
            MainApp.deliveryInProgress = false; // Reset for next delivery
        } else {
            // Just show the map and static markers, no animation marker
            statusLabel.setText("No active delivery. Place an order to track delivery.");
        }

        return layout;
    }

    private void animateDelivery(double farmX, double farmY, double custX, double custY,
                                 Circle deliveryMarker, Label statusLabel) {
        double deltaX = (custX - farmX) / TOTAL_STEPS;
        double deltaY = (custY - farmY) / TOTAL_STEPS;

        Timeline timeline = new Timeline();
        for (int i = 1; i <= TOTAL_STEPS; i++) {
            final int step = i;
            KeyFrame frame = new KeyFrame(Duration.seconds(STEP_DURATION_SEC * step), e -> {
                double newX = farmX + deltaX * step;
                double newY = farmY + deltaY * step;
                deliveryMarker.setCenterX(newX);
                deliveryMarker.setCenterY(newY);

                statusLabel.setText("Delivering... " + (step * 100 / TOTAL_STEPS) + "%");
                if (step == TOTAL_STEPS) {
                    statusLabel.setText("Delivery Complete!");
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delivery Completed");
                        alert.setHeaderText(null);
                        alert.setContentText("Delivery completed successfully!");
                        alert.showAndWait();
                    });
                }
            });
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }
}
