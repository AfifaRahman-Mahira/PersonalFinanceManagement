package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddTransactionController {

    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private RadioButton incomeRadio;
    @FXML private RadioButton expenseRadio;
    @FXML private DatePicker datePicker;
    @FXML private TextField noteField;

    private ToggleGroup typeGroup;
    private DashboardController dashboardController;
    private Transaction selectedTransaction;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setTransactionToEdit(Transaction transaction) {
        this.selectedTransaction = transaction;

        titleField.setText(transaction.getTitle());
        categoryField.setText(transaction.getCategory());
        amountField.setText(String.valueOf(transaction.getAmount()));

        if ("Income".equalsIgnoreCase(transaction.getType())) {
            incomeRadio.setSelected(true);
        } else {
            expenseRadio.setSelected(true);
        }

        datePicker.setValue(transaction.getDate());
        noteField.setText(transaction.getNote());
    }

    @FXML
    private void initialize() {
        typeGroup = new ToggleGroup();
        incomeRadio.setToggleGroup(typeGroup);
        expenseRadio.setToggleGroup(typeGroup);

        incomeRadio.setSelected(true);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void saveTransaction() {
        String title = titleField.getText().trim();
        String category = categoryField.getText().trim();
        String amountText = amountField.getText().trim();
        RadioButton selectedTypeRadio = (RadioButton) typeGroup.getSelectedToggle();
        LocalDate date = datePicker.getValue();
        String note = noteField.getText().trim();

        if (title.isEmpty() || category.isEmpty() || amountText.isEmpty() || selectedTypeRadio == null || date == null) {
            showAlert("Validation Error", "Please fill all required fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Amount must be a valid number.");
            return;
        }

        String type = selectedTypeRadio.getText();
        TransactionDAO dao = new TransactionDAO();
        boolean success;

        if (selectedTransaction != null) {
            selectedTransaction.setTitle(title);
            selectedTransaction.setCategory(category);
            selectedTransaction.setAmount(amount);
            selectedTransaction.setType(type);
            selectedTransaction.setDate(date);
            selectedTransaction.setNote(note);
            success = dao.updateTransaction(selectedTransaction);
        } else {
            Transaction transaction = new Transaction(
                    0,
                    title,
                    category,
                    amount,
                    type,
                    date,
                    note,
                    dashboardController.getUserId()
            );
            success = dao.addTransaction(transaction);
        }

        if (success) {
            dashboardController.loadTransactions();
            dashboardController.calculateTotals();
            closeWindow();
        } else {
            showAlert("Error", "Could not save transaction.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
