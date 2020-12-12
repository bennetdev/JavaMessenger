package Client.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Client.data.Client;

public class Main extends Application {

    private static final int DEFAULT_SPACING = 5;
    private Controller con;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        con = new Controller(new Client("Tobi"));
//        scene.getStylesheets().add("path/stylesheet.css");
        primaryStage.setTitle("Hello World");
        VBox root = new VBox();
        root.setPrefSize(1280, 720);
        root.setSpacing(DEFAULT_SPACING);
        root.setPadding(new Insets(DEFAULT_SPACING));
        Scene chatTest = new Scene(root);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username Field");
        root.getChildren().add(usernameField);

        BetterTextArea messageArea = new BetterTextArea() {
            @Override
            public void onEnter() {
                con.sendMessage(this, usernameField);
            }
        };
        messageArea.setPromptText("[Enter] to send, [Ctrl+Enter] for new line");
        root.getChildren().add(messageArea);

        primaryStage.setScene(chatTest);
        primaryStage.show();
    }
}
