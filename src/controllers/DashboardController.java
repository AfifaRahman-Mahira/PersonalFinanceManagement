package controllers;

import database.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colDescription; // Actually shows 'title'
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, String> colNote;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label remainingBudgetLabel;

    private int currentUserId;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    public void setLoggedInUser(User user) {
        this.currentUserId = user.getId();
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        loadTransactions();
        calculateTotals();
    }

    public void loadTransactions() {
        transactionList.clear();

        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date sqlDate = rs.getDate("date");
                LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setTitle(rs.getString("title")); // fixed
                transaction.setCategory(rs.getString("category"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setType(rs.getString("type"));
                transaction.setDate(localDate);
                transaction.setNote(rs.getString("note"));

                transactionList.add(transaction);
            }

            transactionTable.setItems(transactionList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void calculateTotals() {
        double totalIncome = 0, totalExpense = 0;

        for (Transaction t : transactionList) {
            if ("Income".equalsIgnoreCase(t.getType())) {
                totalIncome += t.getAmount();
            } else if ("Expense".equalsIgnoreCase(t.getType())) {
                totalExpense += t.getAmount();
            }
        }

        totalIncomeLabel.setText(String.format("%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("%.2f", totalExpense));
        remainingBudgetLabel.setText(String.format("%.2f", totalIncome - totalExpense));
    }

    public void handleAddTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_transaction.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Add Transaction");
            stage.initModality(Modality.APPLICATION_MODAL);

            AddTransactionController controller = loader.getController();
            controller.setUserId(currentUserId, this);

            stage.showAndWait();

            loadTransactions();
            calculateTotals();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load Add Transaction window.");
        }
    }

    public void handleSetBudget() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Set Budget");
        alert.setHeaderText(null);
        alert.setContentText("Budget feature coming soon!");
        alert.showAndWait();
    }

    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Login");

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to logout.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(new PropertyValueFactory<>("title")); // fixed here!
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
    }
}
