package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label fullnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;

    public void setUserData(String username, String fullname, String email, String phone) {
        welcomeLabel.setText("Welcome, " + username + "!");
        fullnameLabel.setText("Full Name: " + fullname);
        emailLabel.setText("Email: " + email);
        phoneLabel.setText("Phone: " + phone);
    }
}
