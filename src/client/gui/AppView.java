package client.gui;

import client.data.Chat;
import client.data.Client;
import client.data.Controller;
import client.gui.customComponents.ChatToolBar;
import client.gui.customComponents.ChatView;
import client.gui.customComponents.ConnectedIcon;
import client.gui.customComponents.ToolHBox;
import client.gui.customComponents.borderless.BorderlessScene;
import client.gui.customComponents.borderless.CustomStage;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;


/*
The central GUI class. It builds the main windows and calls the loginView. This class is currently effectively static.
It manages the ChatViews, has some utility Methods and is sort of a Main-Class for the GUI. It is barely even active
after initialization is complete.
 */
public class AppView {

    public static final int TOOL_BAR_HEIGHT = 60, SEARCH_BAR_HEIGHT = TOOL_BAR_HEIGHT / 2;

    public static final Color SLIGHT_HIGHLIGHT_COLOR = Color.rgb(163, 210, 255, 0.5);
    public static final Color CLIENT_COLOR = Color.rgb(163, 210, 255).brighter();
    public static final DateTimeFormatter HOUR_MINUTE = DateTimeFormatter.ofPattern("HH:mm"); //yyyy-MM-dd HH:mm:ss a
    public static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Chat openedChat;

    private static BorderlessScene scene;
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
            String result = getController().connectAs(loginView.getUsername(), loginView.getPassword());
            if(result != null) {
                loginView.showError(result);
                return;
            }
            buildAppView();

            getScene().setContent(mainRoot);
            getScene().setResizable(true);
            getScene().setSnapEnabled(true);
            primaryStage.setWidth(mainRoot.getPrefWidth());
            primaryStage.setHeight(mainRoot.getPrefHeight());
        });

        setScene(new BorderlessScene(primaryStage, StageStyle.UNDECORATED,
                loginView, loginView.getPrefWidth(), loginView.getPrefHeight()));
        getScene().setResizable(false);
        getScene().setSnapEnabled(false);
        getScene().setMoveControl(loginView);
        primaryStage.getIcons().add(new Image(Main.RESOURCES + "icon2.png"));
        primaryStage.setScene(getScene());
    }

    private void buildAppView() {
        mainRoot = new GridPane();

        if(getScene() == null) {
            setScene(new BorderlessScene(primaryStage, StageStyle.UNDECORATED,
                    mainRoot, 300, 200));
            getScene().setResizable(true);
            getScene().setSnapEnabled(true);
            getScene().setTransparentWindowStyle("-fx-background-color:rgb(200,200,200,0.15);" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,1), 20, 0, 0, 0);" +
                    "-fx-background-insets: 10px;");
            primaryStage.getIcons().add(new Image(Main.RESOURCES + "icon2.png"));
            primaryStage.setScene(getScene());
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
        getScene().setMoveControl(navToolBar);
        navigationSide.getChildren().add(navToolBar);

        Label clientLabel = new Label();
        clientLabel.setMinWidth(Region.USE_PREF_SIZE);
        clientLabel.setStyle("-fx-text-fill: black;" +
                             "-fx-font-weight: bold;");
        clientLabel.textProperty().bind(client.getNameProperty());
        navToolBar.getChildren().add(clientLabel);

        ConnectedIcon connectedIcon = new ConnectedIcon(getClient().isConnected(), getClient().getLatestServerResponse());
        InvalidationListener connectionChangeListener = e -> {
            connectedIcon.setConnected(getClient().isConnected(), getClient().getLatestServerResponse()
                    + "\n- click to retry login");
        };
        getClient().getConnectedProperty().addListener(connectionChangeListener);
        connectedIcon.setOnMouseClicked(e -> {
            if(!getClient().isConnected()) getController().connect();
        });
        navToolBar.getChildren().add(connectedIcon);

        Button logoutButton = new Button("Logout");
        logoutButton.setFocusTraversable(false);
        logoutButton.getStyleClass().add("extra-flat-button");
        logoutButton.setGraphic(new ImageView(Main.RESOURCES + "power.png"));
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
                        chat.getUsername().toLowerCase().contains(navSearchField.getText().toLowerCase())
                                || (chat.getLastMessage() != null
                                && chat.getLastMessage().getText().toLowerCase().contains(
                                        navSearchField.getText().toLowerCase()
                                ))
                );
            }
        });

        ChatNavigationList navChatSelectPane = new ChatNavigationList(new VBox(), client, this);
        navigationSide.getChildren().add(navChatSelectPane);

        chatSide = new VBox();
        mainRoot.add(chatSide, 1, 0);

        ChatToolBar chatToolBar = new ChatToolBar(this, getScene());
        chatSide.getChildren().add(chatToolBar);

    }

    public void openChat(Chat chat) {
        if(openedChat != null) mainRoot.getChildren().remove(openedChat.getChatView());
        if(chat == null) {
            mainRoot.getChildren().remove(chatSide);
            mainRoot.add(chatSide, 1, 0);
        } else if(chat.getChatView() == null) {
            mainRoot.getChildren().remove(chatSide);
            chat.setChatView(new ChatView(chat, client, this, getScene()));
            mainRoot.add(chat.getChatView(), 1, 0);
            Platform.runLater(() -> openedChat.getChatView().getWriteMessageTextArea().requestFocus());
            Platform.runLater(() -> chat.getChatView().chatMessagesView.setVvalue(1));
        } else {
            mainRoot.add(chat.getChatView(), 1, 0);
            Platform.runLater(() -> openedChat.getChatView().getWriteMessageTextArea().requestFocus());
        }
        openedChat = chat;
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

    public static BorderlessScene getScene() {
        return scene;
    }

    public static void setScene(BorderlessScene scene) {
        AppView.scene = scene;
    }
}
