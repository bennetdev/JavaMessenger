package client.gui;

import javafx.application.Platform;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppView {

    private Controller con;


    private static final int    TOOL_BAR_HEIGHT = 60,
                                SEARCH_BAR_HEIGHT = TOOL_BAR_HEIGHT / 2,
                                SCROLL_PANE_FONT_SIZE = 16;

    private double xOffset, yOffset;

    private Stage primaryStage;


    public AppView(Stage primaryStage, Controller controller) {
        con = controller;
        this.primaryStage = primaryStage;
        buildGUI(primaryStage);
    }

    public void buildGUI(Stage primaryStage) {
        primaryStage.setTitle("Hermes Messenger");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        GridPane root = new GridPane();
        root.setPrefSize(1280, 720);
        Scene chatTest = new Scene(root);
        // chatTest.getStylesheets().add("path/stylesheet.css");
        primaryStage.setScene(chatTest);

        // Split up into two more methods to keep code clean
        VBox navigationSide = new VBox();
        navigationSide(navigationSide);
        root.add(navigationSide, 0, 0);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(30);
        cc1.setFillWidth(true);
        root.getColumnConstraints().add(cc1);

        // Split up into two more methods to keep code clean
        VBox chatSide = new VBox();
        chatSide(chatSide);
        root.add(chatSide, 1, 0);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(70);
        cc2.setFillWidth(true);
        root.getColumnConstraints().add(cc2);

        primaryStage.show();
    }

    private void navigationSide(VBox navigationSide) {
        ToolBar navToolBar = new ToolBar();
        navToolBar.setPrefHeight(TOOL_BAR_HEIGHT);
        addWindowDraggingFunctionality(primaryStage, navToolBar);
        navigationSide.getChildren().add(navToolBar);

        Button b = new Button("lol");

        navigationSide.getChildren().add(new Separator());

        TextField navSearchField = (TextField) makeQuickTextControl(new TextField());
        navSearchField.setPromptText("Search");
    }

    private void chatSide(VBox chatSide) {

    }

    private TextInputControl makeQuickTextControl(TextInputControl textInputControl) {
        textInputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) Platform.runLater(textInputControl::selectAll);
        });
        return textInputControl;
    }

    private void addWindowDraggingFunctionality(Stage stage, Node node) {
        node.setOnMousePressed((MouseEvent e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        node.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }
}
