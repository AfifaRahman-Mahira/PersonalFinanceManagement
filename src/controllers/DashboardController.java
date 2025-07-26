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
import controllers.Transaction;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DashboardController {

    @FXML private Label welcomeLabel;

    // Budget Labels
    @FXML private Label totalBudgetLabel;
    @FXML private Label foodBudgetLabel;
    @FXML private Label transportBudgetLabel;
    @FXML private Label shoppingBudgetLabel;
    @FXML private Label otherBudgetLabel;

    // Transaction Table
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, String> colNote;

    // Summary Labels
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label remainingBalanceLabel;
@FXML private Label totalRemainingLabel;
@FXML private Label foodRemainingLabel;
@FXML private Label transportRemainingLabel;
@FXML private Label shoppingRemainingLabel;
@FXML private Label otherRemainingLabel;

    // Currency Converter
    @FXML private TextField amountField;
    @FXML private ComboBox<String> fromCurrencyCombo;
    @FXML private ComboBox<String> toCurrencyCombo;
    @FXML private Label convertedResultLabel;

    private int currentUserId;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    public int getUserId() {
        return currentUserId;
    }

    public void setLoggedInUser(User user) {
        this.currentUserId = user.getId();
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        loadTransactions();
        calculateTotals();
        loadBudgets();
    }

   

private double totalBudget = 0, foodBudget = 0, transportBudget = 0, shoppingBudget = 0, otherBudget = 0;
private double totalSpent = 0, foodSpent = 0, transportSpent = 0, shoppingSpent = 0, otherSpent = 0;

public void loadBudgets() {
    try (Connection conn = DBConnector.getConnection()) {
        String budgetQuery = "SELECT category, monthly_limit FROM budget WHERE user_id = ?";
        PreparedStatement budgetStmt = conn.prepareStatement(budgetQuery);
        budgetStmt.setInt(1, currentUserId);
        ResultSet budgetRs = budgetStmt.executeQuery();

        // বাজেট লোড
        while (budgetRs.next()) {
            String cat = budgetRs.getString("category");
            double limit = budgetRs.getDouble("monthly_limit");
            switch (cat) {
                case "Total": totalBudget = limit; break;
                case "Food": foodBudget = limit; break;
                case "Transport": transportBudget = limit; break;
                case "Shopping": shoppingBudget = limit; break;
                case "Other": otherBudget = limit; break;
            }
        }

        // খরচ লোড
        String expenseQuery = "SELECT category, SUM(amount) as spent FROM transactions WHERE user_id = ? AND type = 'Expense' GROUP BY category";
        PreparedStatement expenseStmt = conn.prepareStatement(expenseQuery);
        expenseStmt.setInt(1, currentUserId);
        ResultSet expenseRs = expenseStmt.executeQuery();

        while (expenseRs.next()) {
            String cat = expenseRs.getString("category");
            double spent = expenseRs.getDouble("spent");
            switch (cat) {
                case "Food": foodSpent = spent; break;
                case "Transport": transportSpent = spent; break;
                case "Shopping": shoppingSpent = spent; break;
                case "Other": otherSpent = spent; break;
            }
        }

        // মোট খরচ হিসাব (totalSpent)
        totalSpent = foodSpent + transportSpent + shoppingSpent + otherSpent;

        // লেবেল আপডেট
        totalBudgetLabel.setText("৳ " + String.format("%.2f", totalBudget));
        foodBudgetLabel.setText("৳ " + String.format("%.2f", foodBudget));
        transportBudgetLabel.setText("৳ " + String.format("%.2f", transportBudget));
        shoppingBudgetLabel.setText("৳ " + String.format("%.2f", shoppingBudget));
        otherBudgetLabel.setText("৳ " + String.format("%.2f", otherBudget));

        totalRemainingLabel.setText("৳ " + String.format("%.2f", totalBudget - totalSpent));
        foodRemainingLabel.setText("৳ " + String.format("%.2f", foodBudget - foodSpent));
        transportRemainingLabel.setText("৳ " + String.format("%.2f", transportBudget - transportSpent));
        shoppingRemainingLabel.setText("৳ " + String.format("%.2f", shoppingBudget - shoppingSpent));
        otherRemainingLabel.setText("৳ " + String.format("%.2f", otherBudget - otherSpent));

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void setBudgetValues(double total, double food, double transport, double shopping, double other) {
        if (totalBudgetLabel != null) totalBudgetLabel.setText("৳ " + total);
        if (foodBudgetLabel != null) foodBudgetLabel.setText("৳ " + food);
        if (transportBudgetLabel != null) transportBudgetLabel.setText("৳ " + transport);
        if (shoppingBudgetLabel != null) shoppingBudgetLabel.setText("৳ " + shopping);
        if (otherBudgetLabel != null) otherBudgetLabel.setText("৳ " + other);
    }

    public void loadTransactions() {
        transactionList.clear();
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate localDate = rs.getDate("date").toLocalDate();
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
        remainingBalanceLabel.setText(String.format("%.2f", totalIncome - totalExpense));
    }

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        if (fromCurrencyCombo != null && toCurrencyCombo != null) {
            fromCurrencyCombo.setItems(FXCollections.observableArrayList("USD", "BDT", "EUR"));
            toCurrencyCombo.setItems(FXCollections.observableArrayList("USD", "BDT", "EUR"));
        }
    }

    @FXML
    private void handleCurrencyConvert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = fromCurrencyCombo.getValue();
            String to = toCurrencyCombo.getValue();
            double rate = getExchangeRate(from, to);
            double converted = amount * rate;
            convertedResultLabel.setText("Converted Amount: " + String.format("%.2f", converted) + " " + to);
        } catch (Exception e) {
            convertedResultLabel.setText("Invalid input.");
        }
    }

    private double getExchangeRate(String from, String to) {
        if (from.equals(to)) return 1.0;
        if (from.equals("USD") && to.equals("BDT")) return 110.0;
        if (from.equals("BDT") && to.equals("USD")) return 0.0091;
        if (from.equals("EUR") && to.equals("BDT")) return 120.0;
        if (from.equals("BDT") && to.equals("EUR")) return 0.0083;
        return 1.0;
    }

    @FXML
    private void handleEditTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a transaction to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditTransaction.fxml"));
            Parent root = loader.load();
            EditTransactionController controller = loader.getController();
            controller.setTransaction(selected);
            controller.setDashboardController(this);
            Stage stage = new Stage();
            stage.setTitle("Edit Transaction");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit window.");
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
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a transaction to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Transaction");
        confirm.setContentText("Are you sure?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DBConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {

                stmt.setInt(1, selected.getId());
                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    transactionList.remove(selected);
                    calculateTotals();
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Transaction deleted.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleOpenSetBudget() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/set_budget.fxml"));
            Parent root = loader.load();

            SetBudgetController controller = loader.getController();
            controller.setUserId(currentUserId); // ইউজার আইডি সেট করো
            controller.setDashboardController(this);  // **এই লাইনটি খুব গুরুত্বপূর্ণ**

            Stage stage = new Stage();
            stage.setTitle("Set Budget");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // উইন্ডো বন্ধ হলে নিজেও আপডেট হতে পারো
            loadBudgets();
            loadTransactions();
            calculateTotals();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
