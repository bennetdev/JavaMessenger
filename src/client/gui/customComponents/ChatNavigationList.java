package client.gui.customComponents;

import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
 */
public class ChatNavigationList extends ScrollPane {

    private ChatHBox previousSelectionTarget;
    private HBox previousPreSelectionTarget;

    public static final Background FOCUSED_BACKGROUND =
            new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Background PRE_FOCUSED_BACKGROUND =
            new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.grayscale(), CornerRadii.EMPTY, Insets.EMPTY));

    public ChatNavigationList(Client client, AppView appView) {
        super();

        setPrefHeight(42069);
        setFitToWidth(true);
        setFocusTraversable(false);

        VBox root = new VBox();
        root.setFocusTraversable(false);

        ContextMenu cellContextMenu = new ContextMenu();
        MenuItem deleteChatMenuItem = new MenuItem("Delete Chat with \"\"");
        deleteChatMenuItem.setGraphic(new ImageView(AppView.RESOURCES + "delete.png"));
        deleteChatMenuItem.setOnAction(e -> appView.getController().deleteSelectedChat(appView, appView.openedChat));
        cellContextMenu.getItems().add(deleteChatMenuItem);
        root.setOnContextMenuRequested(e -> {
            appView.openedChat = previousSelectionTarget.getChat();
            deleteChatMenuItem.setText("Delete Chat with " + appView.openedChat.getUserName() + "");
            cellContextMenu.show(appView.openedChat.getChatHBox(), e.getScreenX(), e.getScreenY());
        });
        setContent(root);

        for(Chat chat : client.getChats()) {
            root.getChildren().add(0, buildCell(chat, appView));
        }

        client.getChats().addListener((ListChangeListener<? super Chat>) change -> {
            change.next();
            Chat lastChat = null;
            for(Chat chat : change.getAddedSubList()) {
                root.getChildren().add(0, buildCell(chat, appView));
                lastChat = chat;
            }
            if(lastChat != null) {
                appView.openChat(lastChat);
                if(previousSelectionTarget != null) previousSelectionTarget.setBackground(null);
                previousSelectionTarget = lastChat.getChatHBox();
                previousSelectionTarget.setBackground(FOCUSED_BACKGROUND);
            }
            setVvalue(0);
        });

        root.getChildren().add(new Separator(Orientation.HORIZONTAL));

        HBox addUserCell = new HBox();
        addUserCell.setMinHeight(60);
        addUserCell.setPadding(new Insets(3, 3, 3, 3));
        addUserCell.setSpacing(3);
        root.getChildren().add(addUserCell);

        VBox vSplitter = new VBox();
        addUserCell.getChildren().add(vSplitter);

        vSplitter.getChildren().add(new Label("New Chat"));

        HBox hSplitter = new HBox();
        hSplitter.setSpacing(2);
        vSplitter.getChildren().add(hSplitter);

        TextField usernameTextField = new TextField();
        usernameTextField.setOnAction(e -> {
            client.getChats().add(new Chat(usernameTextField.getText()));
            usernameTextField.setText("");
        });
        usernameTextField.focusedProperty().addListener(e -> {
            if(usernameTextField.isFocused()) addUserCell.setBackground(FOCUSED_BACKGROUND);
            else addUserCell.setBackground(null);
        });
        addUserCell.setOnMousePressed(e -> usernameTextField.requestFocus());
        usernameTextField.setPromptText("Username");
        hSplitter.getChildren().add(usernameTextField);

        Button addChatButton = new Button("Add");
        addChatButton.setFocusTraversable(false);
        addChatButton.setOnAction(e -> {
            client.getChats().add(new Chat(usernameTextField.getText()));
            usernameTextField.setText("");
        });
        addChatButton.setMinWidth(50);
        hSplitter.getChildren().add(addChatButton);
    }


    private ChatHBox buildCell(Chat chat, AppView appView) {
        ChatHBox cell = new ChatHBox(chat);
        cell.setFocusTraversable(true);
        cell.managedProperty().bind(cell.visibleProperty());

        cell.setOnMousePressed(e -> {
            if(previousSelectionTarget != null && previousSelectionTarget != cell) previousSelectionTarget.setBackground(null);
            cell.requestFocus();
            appView.openChat(cell.getChat());
            cell.setBackground(FOCUSED_BACKGROUND);
            previousSelectionTarget = cell;
        });
        cell.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                if(previousSelectionTarget != null && previousSelectionTarget != cell) previousSelectionTarget.setBackground(null);
                appView.openChat(cell.getChat());
                cell.requestFocus();
                cell.setBackground(FOCUSED_BACKGROUND);
                previousSelectionTarget = cell;
            }
        });
        cell.focusedProperty().addListener(e -> {
            if(cell.isFocused()) {
                if(cell.getChat() == appView.openedChat) {
                    cell.setBackground(FOCUSED_BACKGROUND);
                }
                else cell.setBackground(PRE_FOCUSED_BACKGROUND);
            } else {
                if(cell.getChat() != appView.openedChat) cell.setBackground(null);
            }
        });
        cell.setMinHeight(60);
        cell.setPadding(new Insets(3, 3, 3, 3));
        cell.setSpacing(8);

        Rectangle rectangle = new Rectangle(60d / 2d, 60d / 1.3d);

        StackPane stack = new StackPane();
        stack.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
        cell.getChildren().add(stack);

        ColorPicker userColorPicker = new ColorPicker();
        userColorPicker.setFocusTraversable(false);
        userColorPicker.setMinHeight(rectangle.getHeight());
        userColorPicker.valueProperty().bindBidirectional(chat.getColorProperty());
        stack.getChildren().addAll(userColorPicker, rectangle);

        rectangle.setMouseTransparent(true); //Mouse clicks the ColorPicker, not the Rectangle.
        rectangle.setFill(chat.getColor());
        rectangle.fillProperty().bind(userColorPicker.valueProperty());

        VBox nameSplitMessage = new VBox();
        cell.getChildren().add(nameSplitMessage);

        Label name = new Label(chat.getUserName());
        name.textFillProperty().bind(userColorPicker.valueProperty());
        name.setStyle("-fx-font-weight: bold;");
        nameSplitMessage.getChildren().add(name);

        Label lastMessage = new Label();

        //This line can probably be removed if ChatLoading from files will be done only AFTER this point
        if(chat.getLastMessage() != null) lastMessage.setText(chat.getLastMessage().toStringLastMessage());

        chat.getMessages().addListener((ListChangeListener<? super Message>) change -> {
            lastMessage.setText(chat.getLastMessage().toStringLastMessage());
            VBox root = (VBox) cell.getParent();
            root.getChildren().remove(cell);
            root.getChildren().add(0, cell);
        });
        nameSplitMessage.getChildren().add(lastMessage);

        return cell;
    }
}
