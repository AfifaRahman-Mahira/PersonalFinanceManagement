package controllers;

import database.DBConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginMessage;

    @FXML private TextField signupUsernameField;
    @FXML private PasswordField signupPasswordField;
    @FXML private TextField signupFullnameField;
    @FXML private TextField signupEmailField;
    @FXML private TextField signupPhoneField;
    @FXML private Label signupMessage;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            loginMessage.setText("Please enter username and password");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                loginMessage.setText("Login successful");

                int userId = rs.getInt("id"); // ধরছি ইউজার টেবিলে id আছে
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setUserData(userId, username, fullname, email, phone);

                Stage stage = (Stage) loginUsernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                loginMessage.setText("Invalid username or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            loginMessage.setText("Database error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String username = signupUsernameField.getText();
        String password = signupPasswordField.getText();
        String fullname = signupFullnameField.getText();
        String email = signupEmailField.getText();
        String phone = signupPhoneField.getText();

        if(username.isEmpty() || password.isEmpty()) {
            signupMessage.setText("Username and password are required");
            return;
        }

        try (Connection conn = DBConnector.getConnection()) {
            String sql = "INSERT INTO users (username, password, fullname, email, phone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, fullname);
            pst.setString(4, email);
            pst.setString(5, phone);

            int rows = pst.executeUpdate();
            if(rows > 0) {
                signupMessage.setText("Signup successful. Please login.");
            } else {
                signupMessage.setText("Signup failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            signupMessage.setText("Database error or username already exists");
        }
    }

    @FXML
    private void goToSignup(ActionEvent event) {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) signupUsernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
