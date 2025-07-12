package controllers;

import database.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AuthController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullnameField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");

                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: green;");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();
                dashboardController.setUser(userId, username);  // এখানে userId পাঠানো হচ্ছে

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
            } else {
                messageLabel.setText("Invalid username or password");
                messageLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            messageLabel.setText("Login failed due to error");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void openSignup(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Sign Up");
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullname = fullnameField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                messageLabel.setText("Username already exists");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String insertSql = "INSERT INTO users (username, password, fullname, email) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, fullname);
            insertStmt.setString(4, email);

            insertStmt.executeUpdate();

            messageLabel.setText("Signup successful! Please login.");
            messageLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Signup failed due to error");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void openLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
    }
}
