package controllers;

import database.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetBudgetController {

    @FXML private TextField totalField, foodField, transportField, shoppingField, otherField;
    @FXML private Label totalSpentLabel, foodSpentLabel, transportSpentLabel, shoppingSpentLabel, otherSpentLabel;
    @FXML private Label totalRemainingLabel, foodRemainingLabel, transportRemainingLabel, shoppingRemainingLabel, otherRemainingLabel;

    private int currentUserId;

    // DashboardController রেফারেন্স রাখতে হবে
    private DashboardController dashboardController;

    // DashboardController সেট করার মেথড
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadBudgetAndExpenses();
    }

    @FXML
    private void handleSaveBudget() {
        saveBudget("Total", totalField.getText());
        saveBudget("Food", foodField.getText());
        saveBudget("Transport", transportField.getText());
        saveBudget("Shopping", shoppingField.getText());
        saveBudget("Other", otherField.getText());

        loadBudgetAndExpenses();

        // ড্যাশবোর্ড আপডেট করো
        if (dashboardController != null) {
            dashboardController.loadBudgets();
            dashboardController.loadTransactions();
            dashboardController.calculateTotals();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("Budget saved successfully!");
        alert.showAndWait();

        // উইন্ডো বন্ধ করো
        Stage stage = (Stage) totalField.getScene().getWindow();
        stage.close();
    }

    private void saveBudget(String category, String valueStr) {
        if (valueStr == null || valueStr.trim().isEmpty()) return;
        try {
            double value = Double.parseDouble(valueStr);
            try (Connection conn = DBConnector.getConnection()) {
                String query = "INSERT INTO budget (user_id, category, monthly_limit) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE monthly_limit = VALUES(monthly_limit)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, currentUserId);
                stmt.setString(2, category);
                stmt.setDouble(3, value);
                stmt.executeUpdate();
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBudgetAndExpenses() {
        try (Connection conn = DBConnector.getConnection()) {
            // Load budgets
            String budgetQuery = "SELECT category, monthly_limit FROM budget WHERE user_id = ?";
            PreparedStatement budgetStmt = conn.prepareStatement(budgetQuery);
            budgetStmt.setInt(1, currentUserId);
            ResultSet budgetRs = budgetStmt.executeQuery();

            double totalBudget = 0, foodBudget = 0, transportBudget = 0, shoppingBudget = 0, otherBudget = 0;

            while (budgetRs.next()) {
                String cat = budgetRs.getString("category");
                double limit = budgetRs.getDouble("monthly_limit");
                switch (cat) {
                    case "Total": totalBudget = limit; break;
                    case "Food": foodBudget = limit; break;
                    case "Transport": transportBudget = limit; break;
                    case "Shopping": shoppingBudget = limit; break;
                    case "Other": otherBudget = limit; break;
                }
            }

            totalField.setText(String.valueOf(totalBudget));
            foodField.setText(String.valueOf(foodBudget));
            transportField.setText(String.valueOf(transportBudget));
            shoppingField.setText(String.valueOf(shoppingBudget));
            otherField.setText(String.valueOf(otherBudget));

            // Load spent
            String expenseQuery = "SELECT category, SUM(amount) as spent FROM transactions WHERE user_id = ? AND type = 'Expense' GROUP BY category";
            PreparedStatement expenseStmt = conn.prepareStatement(expenseQuery);
            expenseStmt.setInt(1, currentUserId);
            ResultSet expenseRs = expenseStmt.executeQuery();

            double totalSpent = 0, foodSpent = 0, transportSpent = 0, shoppingSpent = 0, otherSpent = 0;

            while (expenseRs.next()) {
                String cat = expenseRs.getString("category");
                double spent = expenseRs.getDouble("spent");
                switch (cat) {
                    case "Total": totalSpent = spent; break;
                    case "Food": foodSpent = spent; break;
                    case "Transport": transportSpent = spent; break;
                    case "Shopping": shoppingSpent = spent; break;
                    case "Other": otherSpent = spent; break;
                }
            }

            totalSpentLabel.setText("৳ " + totalSpent);
            foodSpentLabel.setText("৳ " + foodSpent);
            transportSpentLabel.setText("৳ " + transportSpent);
            shoppingSpentLabel.setText("৳ " + shoppingSpent);
            otherSpentLabel.setText("৳ " + otherSpent);

            totalRemainingLabel.setText("৳ " + (totalBudget - totalSpent));
            foodRemainingLabel.setText("৳ " + (foodBudget - foodSpent));
            transportRemainingLabel.setText("৳ " + (transportBudget - transportSpent));
            shoppingRemainingLabel.setText("৳ " + (shoppingBudget - shoppingSpent));
            otherRemainingLabel.setText("৳ " + (otherBudget - otherSpent));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
