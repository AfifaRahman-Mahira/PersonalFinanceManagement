package controllers;

import database.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DashboardController {

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private Label welcomeLabel;

    private int userId;
    private String username;

    public void setUser(int userId, String username) {
        this.userId = userId;
        this.username = username;
        welcomeLabel.setText("Welcome, " + username);
        initializeTableColumns();
        loadTransactions();
    }

    private void initializeTableColumns() {
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
    }

    public void loadTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT description, category, amount FROM transactions WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("amount")
                ));
            }

            transactionTable.setItems(transactions);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_transaction.fxml"));
            Parent root = loader.load();

            AddTransactionController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = new Stage();
            stage.setTitle("Add Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadTransactions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openBudgetPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget.fxml"));
            Parent root = loader.load();

            BudgetController controller = loader.getController();
            controller.setUserId(userId);  // userId পাঠানো হচ্ছে এখানে

            Stage stage = new Stage();
            stage.setTitle("Budget Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
