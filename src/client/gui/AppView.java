package client.gui;

import client.data.Chat;
import client.data.Client;
import client.gui.customComponents.ChatNavigationList;
import client.gui.customComponents.ChatView;
import client.gui.customComponents.borderless.BorderlessScene;
import client.gui.customComponents.borderless.CustomStage;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class AppView {


    public static final int    TOOL_BAR_HEIGHT = 60,
                                SEARCH_BAR_HEIGHT = TOOL_BAR_HEIGHT / 2,
                                SCROLL_PANE_FONT_SIZE = 16;

    public static final Color   SLIGHT_HIGHLIGHT_COLOR = Color.rgb(163, 210, 255, 0.5);
    public static final DateTimeFormatter HOUR_MINUTE = DateTimeFormatter.ofPattern("HH:mm"); //yyyy-MM-dd HH:mm:ss a
    public static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Chat openedChat;

    private BorderlessScene scene;
    private final Client client;
    private Controller controller;
    private GridPane root;
    private VBox chatSide;
    private static Stage primaryStage;

    public AppView(CustomStage primaryStage, Controller controller, Client client) {
        this.client = client;
        this.setController(controller);
        buildGUI(primaryStage);
    }

    public void buildGUI(CustomStage primaryStage) {
        AppView.primaryStage = primaryStage;
        primaryStage.setTitle("Hermes Messenger");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        root = new GridPane();
        root.setStyle("-fx-border-style: solid inside;" +
                      "-fx-border-color: #6a96c0;" +
                      "-fx-border-width: 2px;" +
                      "-fx-font-size: 14px;");
        root.setGridLinesVisible(true);
        root.setPrefSize(1280, 720);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(30);
        cc1.setFillWidth(true);
        root.getColumnConstraints().add(cc1);

        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(70);
        cc2.setFillWidth(true);
        root.getColumnConstraints().add(cc2);

        scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, root, 400, 250);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN).match(keyEvent)) {
                if(!(openedChat == null || openedChat.getChatView() == null)) {
                    refresh();
                    keyEvent.consume();
                }
            }
        });
        scene.maximizedProperty().addListener(e -> refresh());
        scene.snappedProperty().addListener(e -> refresh());

        scene.setTransparentWindowStyle("-fx-background-color:rgb(200,200,200,0.15);" +
                                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1), 20, 0, 0, 0);" +
                                        "-fx-background-insets: 10px;");
        primaryStage.setScene(scene);

        VBox navigationSide = new VBox();
        navigationSide.setStyle("-fx-border-style: solid inside;" +
                                "-fx-border-color: lightgrey;");

        ToolBar navToolBar = new ToolBar();
        navToolBar.setMinHeight(TOOL_BAR_HEIGHT);
        scene.setMoveControl(navToolBar);
        navigationSide.getChildren().add(navToolBar);

        navigationSide.getChildren().add(defaultSeparator());

        TextField navSearchField = (TextField) makeQuickTextControl(new TextField());
        navSearchField.setMinHeight(SEARCH_BAR_HEIGHT);
        navSearchField.setPromptText("Search");
        navigationSide.getChildren().add(navSearchField);

        ChatNavigationList navChatSelectPane = new ChatNavigationList(client, this);
        navigationSide.getChildren().add(navChatSelectPane);
        root.add(navigationSide, 0, 0);

        navSearchField.textProperty().addListener(e -> {
            for(Chat chat : client.getChats()) {
                chat.getChatHBox().setVisible(
                        chat.getUserName().toLowerCase().contains(navSearchField.getText().toLowerCase())
                        || (chat.getLastMessage() != null
                        && chat.getLastMessage().getText().toLowerCase().contains(navSearchField.getText().toLowerCase()))
                );
            }
        });

        chatSide = new VBox();
        chatSide.setStyle("-fx-border-style: solid inside;" +
                          "-fx-border-color: lightgrey;");
        root.add(chatSide, 1, 0);

        ToolBar chatToolBar = new ToolBar();
        chatToolBar.setMinHeight(AppView.TOOL_BAR_HEIGHT);
        scene.setMoveControl(chatToolBar);
        chatSide.getChildren().add(chatToolBar);

        primaryStage.showAndAdjust();
    }

    public static void refresh() {
        Main.executor.schedule(
                () -> primaryStage.setWidth(primaryStage.getWidth() + 1), 100, TimeUnit.MILLISECONDS);

        Main.executor.schedule(
                () -> primaryStage.setWidth(primaryStage.getWidth() - 1), 300, TimeUnit.MILLISECONDS);
    }

    @NotNull
    public void openChat(Chat chat) {

        if(chat.getChatView() == null) {
            root.getChildren().remove(chatSide);
            //chatSide isn't used after this point, as there will always be a chat opened from now on.
            chatSide = null;
            chat.setChatView(new ChatView(chat, client, this, scene));
        }

        if(openedChat != null) root.getChildren().remove(openedChat.getChatView());
        root.add(chat.getChatView(), 1, 0);
        openedChat = chat;

        refresh();
    }

    private TextInputControl makeQuickTextControl(TextInputControl textInputControl) {
        textInputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) Platform.runLater(textInputControl::selectAll);
        });
        return textInputControl;
    }

    //Creates new instance of a default separator
    public static Separator defaultSeparator() {
        Separator s = new Separator(Orientation.HORIZONTAL);
        s.setVisible(false);
        double height = 6;
        s.setPrefHeight(height);
        s.setMinHeight(height);
        s.setMaxHeight(height);
        return s;
    }

    public static Separator slimSeparator() {
        Separator s = new Separator(Orientation.HORIZONTAL);
        s.setVisible(false);
        double height = 4;
        s.setPrefHeight(height);
        s.setMinHeight(height);
        s.setMaxHeight(height);
        return s;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
