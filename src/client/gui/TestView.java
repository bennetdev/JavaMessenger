package client.gui;

import client.gui.customComponents.BetterTextArea;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestView {

    private static final int DEFAULT_SPACING = 5;
    private Controller con;
    public TextArea lastMessageReceived;

    public TestView(Stage primaryStage, Controller controller) {
        con = controller;
        con.tView = this;
        buildGUI(primaryStage);
    }

    public void buildGUI(Stage primaryStage) {
//        scene.getStylesheets().add("path/stylesheet.css");
        primaryStage.setTitle("Hermes Test");
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

        lastMessageReceived = new TextArea();
        lastMessageReceived.setEditable(false);
        lastMessageReceived.setPromptText("Received Messages.   User = " + con.client.getName());
        lastMessageReceived.setTooltip(new Tooltip("lastMessageReceived"));
        root.getChildren().add(lastMessageReceived);

        primaryStage.setScene(chatTest);
        primaryStage.show();
    }
}
