package client.gui;

import client.data.Chat;
import client.data.Client;
import client.gui.customComponents.*;
import client.gui.customComponents.borderless.BorderlessScene;
import client.gui.customComponents.borderless.CustomStage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class AppView {


    public static final int TOOL_BAR_HEIGHT = 60, SEARCH_BAR_HEIGHT = TOOL_BAR_HEIGHT / 2;

    public static final Color SLIGHT_HIGHLIGHT_COLOR = Color.rgb(163, 210, 255, 0.5);
    public static final Color CLIENT_COLOR = Color.rgb(163, 210, 255).brighter();
    public static final DateTimeFormatter HOUR_MINUTE = DateTimeFormatter.ofPattern("HH:mm"); //yyyy-MM-dd HH:mm:ss a
    public static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final String RESOURCES = "/client/gui/resources/";

    public Chat openedChat;

    private BorderlessScene scene;
    private final Client client;
    private Controller controller;
    private GridPane mainRoot;
    private VBox chatSide;
    public static Stage primaryStage;
    public HBox navToolBar;

    public AppView(CustomStage primaryStage, Controller controller, Client client) {
        this.client = client;
        setController(controller);

        AppView.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        controller.readLoginData();
        if(controller.logout) {
            buildLoginView();
        } else {
            getController().connect();
            buildAppView();
        }

        primaryStage.showAndAdjust();
    }

    private void buildLoginView() {
        LoginView loginView = new LoginView();
        loginView.getLoginButton().setOnAction(e -> {
            getController().connectAs(loginView.getUsername(), loginView.getPassword());

            buildAppView();

            scene.setContent(mainRoot);
            scene.setResizable(true);
            scene.setSnapEnabled(true);
            primaryStage.setWidth(mainRoot.getPrefWidth());
            primaryStage.setHeight(mainRoot.getPrefHeight());
        });

        scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED,
                loginView, loginView.getPrefWidth(), loginView.getPrefHeight());
        scene.setResizable(false);
        scene.setSnapEnabled(false);
        scene.setMoveControl(loginView);
        primaryStage.setScene(scene);
    }

    private void buildAppView() {
        mainRoot = new GridPane();

        if(scene == null) {
            scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED,
                    mainRoot, 300, 200);
            scene.setResizable(true);
            scene.setSnapEnabled(true);
            scene.maximizedProperty().addListener(e -> refresh());
            scene.snappedProperty().addListener(e -> refresh());
            scene.setTransparentWindowStyle("-fx-background-color:rgb(200,200,200,0.15);" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1), 20, 0, 0, 0);" +
                    "-fx-background-insets: 10px;");
            primaryStage.setScene(scene);
        }

//        mainRoot = new GridPane();
        mainRoot.setStyle("-fx-border-style: solid inside;" +
                "-fx-border-color: " + toCSSColor(CLIENT_COLOR) + ";" +
                "-fx-border-width: 3px;" +
                "-fx-font-size: 14px;");
        mainRoot.setGridLinesVisible(false);
        mainRoot.setPrefSize(600, 500);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setPercentWidth(30);
        cc1.setFillWidth(true);
        mainRoot.getColumnConstraints().add(cc1);

        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(70);
        cc2.setFillWidth(true);
        mainRoot.getColumnConstraints().add(cc2);

        VBox navigationSide = new VBox();
        mainRoot.add(navigationSide, 0, 0);

        navToolBar = new ToolHBox();
        navToolBar.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(239, 247, 255), #cce6ff);");
        navToolBar.setPadding(new Insets(5));
        navToolBar.setMinHeight(TOOL_BAR_HEIGHT);
        scene.setMoveControl(navToolBar);
        navigationSide.getChildren().add(navToolBar);

        Label clientLabel = new Label(client.getNameProperty().getValue());
        clientLabel.setMinWidth(Region.USE_PREF_SIZE);
        clientLabel.setStyle("-fx-text-fill: black;" +
                             "-fx-font-weight: bold;");
        client.getNameProperty().addListener(e -> clientLabel.setText(client.getNameProperty().getValue()));
        navToolBar.getChildren().add(clientLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setFocusTraversable(false);
        logoutButton.getStyleClass().add("extra-flat-button");
        logoutButton.setGraphic(new ImageView(AppView.RESOURCES + "power.png"));
        logoutButton.setOnAction(e -> {
            controller.logout = true;
            Platform.exit();
        });
        navToolBar.getChildren().add(logoutButton);

        TextField navSearchField = (TextField) makeQuickTextControl(new TextField());
        navSearchField.setMinHeight(SEARCH_BAR_HEIGHT);
        navSearchField.setPromptText("Search");
        navigationSide.getChildren().add(navSearchField);
        navSearchField.textProperty().addListener(e -> {
            for(Chat chat : client.getChats()) {
                chat.getChatHBox().setVisible(
                        chat.getUserName().toLowerCase().contains(navSearchField.getText().toLowerCase())
                                || (chat.getLastMessage() != null
                                && chat.getLastMessage().getText().toLowerCase().contains(navSearchField.getText().toLowerCase()))
                );
            }
        });

        ChatNavigationList navChatSelectPane = new ChatNavigationList(client, this);
        navigationSide.getChildren().add(navChatSelectPane);

        chatSide = new VBox();
        mainRoot.add(chatSide, 1, 0);

        ChatToolBar chatToolBar = new ChatToolBar(this, scene);
        chatSide.getChildren().add(chatToolBar);

    }

    public static void refresh() {
        Platform.runLater(() -> {
            Main.executor.schedule(
                    () -> primaryStage.setWidth(primaryStage.getWidth() + 2), 75, TimeUnit.MILLISECONDS
            );

            Main.executor.schedule(() -> {
                        primaryStage.setWidth(primaryStage.getWidth() - 2);

                    }, 150, TimeUnit.MILLISECONDS
            );
        });
    }

    public static void refresh(ChatView.ChatMessagesView scrollPane) {
        refresh();
        Platform.runLater(() -> Main.executor.schedule(() -> scrollPane.setVvalue(1), 200, TimeUnit.MILLISECONDS));
    }

    public void openChat(Chat chat) {
        if(openedChat != null) mainRoot.getChildren().remove(openedChat.getChatView());
        if(chat == null) {
            mainRoot.getChildren().remove(chatSide);
            mainRoot.add(chatSide, 1, 0);
        } else if(chat.getChatView() == null) {
            mainRoot.getChildren().remove(chatSide);
            chat.setChatView(new ChatView(chat, client, this, scene));
            mainRoot.add(chat.getChatView(), 1, 0);
            Platform.runLater(() -> openedChat.getChatView().getWriteMessageTextArea().requestFocus());
            refresh(chat.getChatView().chatMessagesView);
        } else {
            mainRoot.add(chat.getChatView(), 1, 0);
            Platform.runLater(() -> openedChat.getChatView().getWriteMessageTextArea().requestFocus());
            refresh();
        }
        openedChat = chat;
    }

    public static void goodOrBadTextField(TextField tf, boolean good) {
        if(good) {
            tf.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        } else {
            tf.setStyle("-fx-text-box-border: red;" +
                    "-fx-focus-color: red;" +
                    "-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        }
    }

    public static TextInputControl makeQuickTextControl(TextInputControl textInputControl) {
        textInputControl.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) Platform.runLater(textInputControl::selectAll);
        });
        return textInputControl;
    }

    public static String toCSSColor(Color color) {
        return "#" + color.toString().replaceFirst("0x", "");
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

    public Client getClient() {
        return client;
    }
}
