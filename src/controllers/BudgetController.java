package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BudgetController {

    @FXML private TextField categoryField;
    @FXML private TextField monthlyLimitField;

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void handleSaveBudget() {
        String category = categoryField.getText().trim();
        String monthlyLimitStr = monthlyLimitField.getText().trim();

        if (category.isEmpty() || monthlyLimitStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        double monthlyLimit;

        try {
            monthlyLimit = Double.parseDouble(monthlyLimitStr);
            if (monthlyLimit < 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Monthly limit must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid number for monthly limit.");
            return;
        }

        // TODO: Save budget to DB for loggedInUser with category and monthlyLimit
        // For now just show confirmation

        showAlert(Alert.AlertType.INFORMATION, "Success", "Budget saved successfully.");

        // Close window after saving
        Stage stage = (Stage) categoryField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
