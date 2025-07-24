package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditTransactionController {

    @FXML private TextField titleField;   // changed here
    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteField;

    private Transaction transaction;
    private boolean saved = false;

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;

        // Prefill fields with existing data
        titleField.setText(transaction.getTitle());  // changed here
        categoryField.setText(transaction.getCategory());
        amountField.setText(String.valueOf(transaction.getAmount()));
        typeComboBox.setValue(transaction.getType());
        datePicker.setValue(transaction.getDate());
        noteField.setText(transaction.getNote());
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void handleSave() {
        try {
            String title = titleField.getText();   // changed here
            String category = categoryField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String type = typeComboBox.getValue();
            LocalDate date = datePicker.getValue();
            String note = noteField.getText();

            if (title.isEmpty() || category.isEmpty() || type == null || date == null) {
                showAlert("Please fill in all required fields.");
                return;
            }

            transaction.setTitle(title);  // changed here
            transaction.setCategory(category);
            transaction.setAmount(amount);
            transaction.setType(type);
            transaction.setDate(date);
            transaction.setNote(note);

            saved = true;

            Stage stage = (Stage) titleField.getScene().getWindow();  // changed here
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Amount must be a valid number.");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) titleField.getScene().getWindow();  // changed here
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
