package personalfinanceproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class PersonalFinanceProject extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/fxml/login.fxml");
        if (fxmlLocation == null) {
            throw new RuntimeException("FXML file not found! Check your path.");
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        primaryStage.setTitle("Personal Finance Management");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
