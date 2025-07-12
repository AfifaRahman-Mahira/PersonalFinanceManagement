package controllers;

import database.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import personalfinanceproject.Budget;

import java.sql.*;

public class BudgetController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField limitField;
    @FXML private Label messageLabel;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> categoryColumn;
    @FXML private TableColumn<Budget, Double> limitColumn;

    private int userId = 0;

    public void setUserId(int id) {
        this.userId = id;
        loadBudgets();
    }

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("Food", "Transport", "Entertainment", "Other");

        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        limitColumn.setCellValueFactory(cellData -> cellData.getValue().limitProperty().asObject());
    }

    @FXML
    private void handleSaveBudget() {
        String category = categoryComboBox.getValue();
        String limitText = limitField.getText();

        if (category == null || limitText.isEmpty()) {
            messageLabel.setText("Fill all fields!");
            return;
        }

        try {
            double limit = Double.parseDouble(limitText);
            Connection conn = DBConnector.getConnection();

            String sql = "REPLACE INTO budgets (user_id, category, limit_amount) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            stmt.setDouble(3, limit);
            stmt.executeUpdate();

            messageLabel.setText("Budget saved!");
            loadBudgets();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error saving budget.");
        }
    }

    private void loadBudgets() {
        ObservableList<Budget> list = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnector.getConnection();
            String sql = "SELECT category, limit_amount FROM budgets WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Budget(rs.getString("category"), rs.getDouble("limit_amount")));
            }
            budgetTable.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
