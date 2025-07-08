package controllers;

import database.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label fullnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Button btnAddTransaction;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colNote;

    private int currentUserId;

    public void setUserData(int userId, String username, String fullname, String email, String phone) {
        this.currentUserId = userId;
        welcomeLabel.setText("Welcome, " + username + "!");
        fullnameLabel.setText("Full Name: " + fullname);
        emailLabel.setText("Email: " + email);
        phoneLabel.setText("Phone: " + phone);

        loadTransactions();
    }

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
    }

    private void loadTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        try {
            Connection conn = DBConnector.getConnection();
            String sql = "SELECT type, category, amount, date, note FROM transactions WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toString(),
                        rs.getString("note")
                ));
            }

            transactionTable.setItems(transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_transaction.fxml"));
            Parent root = loader.load();

            AddTransactionController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);

            Stage stage = new Stage();
            stage.setTitle("Add Transaction");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
