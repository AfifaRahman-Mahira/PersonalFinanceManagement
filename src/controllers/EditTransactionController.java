package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditTransactionController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;

    private Transaction transaction;
    private DashboardController dashboardController;

    @FXML
    private void initialize() {
       
        categoryComboBox.getItems().addAll("Food", "Transport", "Shopping", "Utilities");
        typeComboBox.getItems().addAll("Income", "Expense");
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;

      
        titleField.setText(transaction.getTitle());
        categoryComboBox.setValue(transaction.getCategory());
        amountField.setText(String.valueOf(transaction.getAmount()));
        typeComboBox.setValue(transaction.getType());
        datePicker.setValue(transaction.getDate());
        noteArea.setText(transaction.getNote());
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    private void handleUpdate() {
        String title = titleField.getText();
        String category = categoryComboBox.getValue();
        String amountStr = amountField.getText();
        String type = typeComboBox.getValue();
        String note = noteArea.getText();

        if (title.isEmpty() || category == null || amountStr.isEmpty() || type == null || datePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill all required fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Amount Error", "Amount must be a valid number.");
            return;
        }

        transaction.setTitle(title);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(datePicker.getValue());
        transaction.setNote(note);

        TransactionDAO dao = new TransactionDAO();
        boolean success = dao.updateTransaction(transaction);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction updated successfully.");
            dashboardController.refreshTransactionTable();
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Failed to update transaction.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
