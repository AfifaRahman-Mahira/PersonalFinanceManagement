package controllers;

import database.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class AddTransactionController {

    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Label messageLabel;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("Food", "Transport", "Entertainment", "Other");
    }

    @FXML
    private void handleAddTransaction(ActionEvent event) {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        String category = categoryComboBox.getValue();

        if (description.isEmpty() || amountText.isEmpty() || category == null) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid amount");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "INSERT INTO transactions (username, description, category, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, description);
            stmt.setString(3, category);
            stmt.setDouble(4, amount);
            stmt.executeUpdate();

            messageLabel.setText("Transaction added!");
            messageLabel.setStyle("-fx-text-fill: green;");
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to add transaction");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearFields() {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }
}
