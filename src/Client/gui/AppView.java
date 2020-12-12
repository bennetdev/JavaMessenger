package Client.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppView {

    private Controller con;

    public AppView(Stage primaryStage, Controller controller) {
        con = controller;
        buildGUI(primaryStage);
    }

    public void buildGUI(Stage primaryStage) {
//        scene.getStylesheets().add("path/stylesheet.css");
        primaryStage.setTitle("Hermes Messenger");
//        Scene chatTest = new Scene(root);
    }
}
