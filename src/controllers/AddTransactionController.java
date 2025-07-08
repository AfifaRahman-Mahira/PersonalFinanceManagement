package controllers;

import database.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class AddTransactionController {

    @FXML private RadioButton rbIncome;
    @FXML private RadioButton rbExpense;
    @FXML private ToggleGroup typeGroup;
    @FXML private ComboBox<String> comboCategory;
    @FXML private TextField txtAmount;
    @FXML private DatePicker datePicker;
    @FXML private TextArea txtNote;
    @FXML private Button btnSubmit;

    private int currentUserId = -1;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @FXML
    public void initialize() {
        comboCategory.getItems().addAll("Salary", "Food", "Rent", "Transport", "Shopping", "Utilities", "Other");
        typeGroup = new ToggleGroup();
        rbIncome.setToggleGroup(typeGroup);
        rbExpense.setToggleGroup(typeGroup);
    }

    @FXML
    public void handleSubmit() {
        String type = rbIncome.isSelected() ? "Income" : rbExpense.isSelected() ? "Expense" : null;
        String category = comboCategory.getValue();
        String amountText = txtAmount.getText();
        LocalDate date = datePicker.getValue();
        String note = txtNote.getText();

        if (type == null || category == null || amountText.isEmpty() || date == null) {
            showAlert("Please fill in all required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            Connection conn = DBConnector.getConnection();
            String sql = "INSERT INTO transactions (user_id, type, category, amount, date, note) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            stmt.setString(2, type);
            stmt.setString(3, category);
            stmt.setDouble(4, amount);
            stmt.setDate(5, java.sql.Date.valueOf(date));
            stmt.setString(6, note);
            stmt.executeUpdate();

            showAlert("Transaction added successfully!");
            clearForm();
        } catch (NumberFormatException e) {
            showAlert("Amount must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving transaction.");
        }
    }

    private void clearForm() {
        rbIncome.setSelected(false);
        rbExpense.setSelected(false);
        comboCategory.getSelectionModel().clearSelection();
        txtAmount.clear();
        datePicker.setValue(null);
        txtNote.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
