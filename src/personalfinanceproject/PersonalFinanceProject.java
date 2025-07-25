package personalfinanceproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import static javafx.application.Application.launch;

public class PersonalFinanceProject extends Application {

    @Override
    public void start(Stage stage) {
        try {
           
            URL fxmlLocation = getClass().getResource("/fxml/login.fxml");
            
            if (fxmlLocation == null) {
                System.err.println("❌ FXML file not found at /fxml/login.fxml");
                return;
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Personal Finance Project");
            stage.show();

        } catch (Exception e) {
            System.err.println("🚨 Error loading the login.fxml file:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
