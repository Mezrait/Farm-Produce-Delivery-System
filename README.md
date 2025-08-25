# Farm Produce Delivery System

## Table of Contents
1. [Introduction](#introduction)
2. [System Overview](#system-overview)
3. [Main Features](#main-features)
    - [User Management](#user-management)
    - [Product Search and Display](#product-search-and-display)
    - [Customer Portal](#customer-portal)
    - [Farmer Dashboard](#farmer-dashboard)
    - [Delivery System](#delivery-system)
4. [Data Management](#data-management)
5. [Exception Handling](#exception-handling)
6. [Core Logic and Console Components](#core-logic-and-console-components)
7. [Graphical User Interface (GUI)](#graphical-user-interface-gui)
8. [Code Structure](#code-structure)
9. [Challenges Faced](#challenges-faced)
10. [Testing and Validation](#testing-and-validation)
11. [Conclusion](#conclusion)

---

## Introduction
The **Farm Produce Delivery System** is a Java-based desktop application designed to connect local farmers directly with customers. The system allows farmers to list fresh produce and manage deliveries, while customers can browse, subscribe, and receive fresh products.

This project demonstrates practical implementation of object-oriented programming (OOP) concepts, exception handling, file-based data management, and GUI development using JavaFX.

---

## System Overview
The system serves two main types of users:
- **Farmers:** Can manage and list produce, track subscriptions, and handle deliveries.
- **Customers:** Can browse available produce, manage subscriptions, and track orders.

The application uses text files for data storage and handles delivery planning, harvest date tracking, and out-of-season alerts.

---

## Main Features

### User Management
- Login system with `LoginScreen` class.
- `User` base class with shared attributes for `Customer` and `Farmer`.

### Product Search and Display
- Search functionality using `ProductSearch`, `ProductSearchEngine`, and `IProductSearch` interfaces.

### Customer Portal
- `CustomerPortal.java` allows subscription management.
- `Subscription` class tracks customer subscriptions.

### Farmer Dashboard
- `FarmerDashboard` enables adding and updating produce.
- Validates harvest dates with `HarvestDateException`.

### Delivery System
- `DeliverySystem`, `DeliveryMap`, and `DeliveryRecord` manage delivery paths and history.

---

## Data Management
- All data stored in plain text files under `data/`:
  - `customers.txt` – registered customers
  - `farmers.txt` – registered farmers
  - `produce_updated.txt` – product information
- `FileManager` class handles reading and writing of these files.

---

## Exception Handling
Custom exceptions improve robustness:
- `HarvestDateException` – invalid harvest dates
- `OutOfSeasonException` – prevents subscriptions for off-season produce
- `DeliveryUnavailableException` – handles unavailable delivery locations

---

## Core Logic and Console Components
- `Product.java` – manages produce attributes
- `Customer.java` & `Farmer.java` – extend `User` with specific functionalities
- `Subscription.java` – links customers with produce
- `ProductSearch.java` & `ProductSearchEngine.java` – handle search logic
- `DeliverySystem.java` & `DeliveryRecord.java` – manage deliveries and history
- `FileManager.java` – handles file-based storage

---

## Graphical User Interface (GUI)
- Built with **JavaFX**.
- Entry point: `MainApp.java`
- Styles: `style.css`

---

## Code Structure
- Follows OOP principles with classes, inheritance, and interfaces.
- `MainApp.java` controls application flow.
- Interfaces like `IProductSearch` define contracts for implementation.

---

## Challenges Faced
- File-based data management without a database.
- Implementing custom exceptions.
- Designing an intuitive GUI.

---

## Testing and Validation
- Manual testing using mock data.
- Functional tests for login, subscription, delivery.
- Validation of exception handling.

---

## Conclusion
This project provided hands-on experience with Java programming, OOP, GUI development, and file-based data management. Future improvements include migrating to a database backend and enhancing GUI features.

---

## License
This project is licensed under the MIT License.

