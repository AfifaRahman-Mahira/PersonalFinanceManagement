package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddTransactionController {

    @FXML private TextField titleField;   // changed here
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteField;

    private int userId;
    private DashboardController parentController;

    public void setUserId(int userId, DashboardController parentController) {
        this.userId = userId;
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Income", "Expense");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                } else {
                    return null;
                }
            }
        });

        datePicker.setPromptText("yyyy-MM-dd");
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText();  // changed here
        String category = categoryField.getText();
        String amountText = amountField.getText();
        String type = typeComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String note = noteField.getText();

        if(title.isEmpty() || category.isEmpty() || amountText.isEmpty() || type == null || date == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be a number.");
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setTitle(title);  // changed here
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(date);
        transaction.setNote(note);

        boolean success = new TransactionDAO().addTransaction(transaction);
        if(success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully.");
            parentController.loadTransactions(); // Refresh table
            parentController.calculateTotals(); // Update totals
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add transaction.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();  // changed here
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
