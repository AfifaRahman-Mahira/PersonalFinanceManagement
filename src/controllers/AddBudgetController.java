package controllers;

import database.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddBudgetController {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField limitField;

    @FXML
    private Label statusLabel;

    private int userId = LoginSession.getUserId(); // static session class

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("Food", "Transport", "Bills", "Entertainment", "Other");
    }

    @FXML
    private void handleSaveBudget() {
        String category = categoryComboBox.getValue();
        String limitText = limitField.getText();

        if (category == null || limitText.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            BigDecimal limit = new BigDecimal(limitText);

            Connection conn = DBConnector.getConnection();
            String query = "INSERT INTO budget (user_id, category, monthly_limit) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.setBigDecimal(3, limit);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                statusLabel.setText("Budget saved successfully!");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                statusLabel.setText("Failed to save budget.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}
