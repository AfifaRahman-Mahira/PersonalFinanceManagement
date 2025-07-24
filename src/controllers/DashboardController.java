package controllers;

import database.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML private TableColumn<Transaction, String> colDescription;
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

    public int getUserId() {
        return currentUserId;
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
                transaction.setTitle(rs.getString("title"));
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

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
    }

    @FXML
private void handleEditTransaction() {
    Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
    if (selectedTransaction == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Transaction Selected");
        alert.setContentText("Please select a transaction to edit.");
        alert.showAndWait();
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditTransaction.fxml"));
        Parent root = loader.load();

        EditTransactionController controller = loader.getController();
        controller.setTransaction(selectedTransaction);
        controller.setDashboardController(this);

        Stage stage = new Stage();
        stage.setTitle("Edit Transaction");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText("Could not open the edit dialog");
        alert.setContentText("Error: " + e.getMessage());
        alert.showAndWait();
    }
}


    @FXML
    private void handleAddTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_transaction.fxml"));
            Parent root = loader.load();

            AddTransactionController controller = loader.getController();
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Transaction");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTransactionTable() {
        loadTransactions();
        calculateTotals();
    }

    @FXML
    public void handleDeleteTransaction() {
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Transaction Selected");
            alert.setContentText("Please select a transaction to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Transaction");
        confirmAlert.setContentText("Are you sure you want to delete the selected transaction?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            String sql = "DELETE FROM transactions WHERE id = ?";
            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, selectedTransaction.getId());
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    transactionList.remove(selectedTransaction);
                    calculateTotals();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deleted");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Transaction deleted successfully.");
                    successAlert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Could not delete transaction");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}
