package personalfinanceproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PersonalFinanceProject extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Corrected relative path
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml")); // <-- Default Login Page

        primaryStage.setTitle("Personal Finance Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
