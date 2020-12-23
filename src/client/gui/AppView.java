package client.gui;

import client.data.Client;
import client.gui.customComponents.ChatNavigationPane;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.runtime.ECMAException;

public class AppView {


    private static final int    TOOL_BAR_HEIGHT = 60,
                                SEARCH_BAR_HEIGHT = TOOL_BAR_HEIGHT / 2,
                                SCROLL_PANE_FONT_SIZE = 16;

    private double xOffset, yOffset;

    private BorderlessScene scene;
    private Client client;

    public AppView(Stage primaryStage, Controller controller, Client client) {
        this.client = client;
        buildGUI(primaryStage);
    }

    public void buildGUI(Stage primaryStage) {
        primaryStage.setTitle("Hermes Messenger");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        GridPane root = new GridPane();
        root.setStyle("-fx-border-style: solid inside;" +
                      "-fx-border-color: #2b98ff;" +
                      "-fx-border-width: 2px");
        root.setGridLinesVisible(true);
        root.setPrefSize(1280, 720);
        scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, root, 400, 250);
        // chatTest.getStylesheets().add("path/stylesheet.css");
        primaryStage.setScene(scene);

        // Split up into two more methods to keep code clean
        VBox navigationSide = new VBox();
        navigationSide.setStyle("-fx-border-style: solid inside;" +
                                "-fx-border-color: lightgrey;");
        navigationSide(navigationSide);
        root.add(navigationSide, 0, 0);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(30);
        cc1.setFillWidth(true);
        root.getColumnConstraints().add(cc1);

        // Split up into two more methods to keep code clean
        VBox chatSide = new VBox();
        chatSide.setStyle("-fx-border-style: solid inside;" +
                          "-fx-border-color: lightgrey;");
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
        navToolBar.setMinHeight(TOOL_BAR_HEIGHT);
        scene.setMoveControl(navToolBar);
        navigationSide.getChildren().add(navToolBar);

        navigationSide.getChildren().add(defaultSeparator());

        TextField navSearchField = (TextField) makeQuickTextControl(new TextField());
        navSearchField.setMinHeight(SEARCH_BAR_HEIGHT);
        navSearchField.setPromptText("Search");
        navigationSide.getChildren().add(navSearchField);
        try {
            ChatNavigationPane navChatSelectPane = new ChatNavigationPane(client);

            ScrollPane navChatScrollPane = new ScrollPane(navChatSelectPane);
            navChatScrollPane.setFitToWidth(true);
            navChatScrollPane.setStyle("-fx-font-size: 16px");
            navigationSide.getChildren().add(navChatScrollPane);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void chatSide(VBox chatSide) {
        ToolBar chatToolBar = new ToolBar();
        chatToolBar.setMinHeight(TOOL_BAR_HEIGHT);
        scene.setMoveControl(chatToolBar);
        chatSide.getChildren().add(chatToolBar);

        chatSide.getChildren().add(defaultSeparator());
    }

    private TextInputControl makeQuickTextControl(TextInputControl textInputControl) {
        textInputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) Platform.runLater(textInputControl::selectAll);
        });
        return textInputControl;
    }

    private Separator defaultSeparator() {
        Separator s = new Separator(Orientation.HORIZONTAL);
        s.setVisible(false);
        s.setPrefHeight(6);
        s.setMinHeight(6);
        s.setMaxHeight(6);
        return s;
    }
}
