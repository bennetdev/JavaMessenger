package client.gui.customComponents;

import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
 */
public class ChatNavigationList extends ScrollPane {

    private HBox previousSelectionTarget;
    public static final Background FOCUSED_BACKGROUND =
            new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR, CornerRadii.EMPTY, Insets.EMPTY));

    public ChatNavigationList(Client client, AppView appView) {
        super();

        setPrefHeight(42069);
        setFitToWidth(true);

        VBox root = new VBox();
        setContent(root);

        for(Chat chat : client.getChats()) {
            root.getChildren().add(0, buildCell(chat, appView));
        }

        client.getChats().addListener((ListChangeListener<? super Chat>) change -> {
            change.next();
            for(Chat chat : change.getAddedSubList()) {
                root.getChildren().add(0, buildCell(chat, appView));
            }
            setVvalue(0);
        });

        HBox addUserCell = new HBox();
        addUserCell.setOnMousePressed(e -> {
            if(previousSelectionTarget != null) previousSelectionTarget.setBackground(null);
            addUserCell.setBackground(FOCUSED_BACKGROUND);
            previousSelectionTarget = addUserCell;
        });
        addUserCell.setMinHeight(60);
        addUserCell.setPadding(new Insets(3, 3, 3, 3));
        addUserCell.setSpacing(3);
        addUserCell.setStyle("-fx-border-color: grey;");
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
        usernameTextField.setPromptText("Username");
        hSplitter.getChildren().add(usernameTextField);

        Button addChatButton = new Button("Add");
        addChatButton.setOnAction(e -> {
            client.getChats().add(new Chat(usernameTextField.getText()));
            usernameTextField.setText("");
        });
        addChatButton.setMinWidth(50);
        hSplitter.getChildren().add(addChatButton);
    }

    private ChatHBox buildCell(Chat chat, AppView appView) {
        ChatHBox cell = new ChatHBox(chat);
        cell.managedProperty().bind(cell.visibleProperty());
        cell.setOnMousePressed(e -> {
            if(previousSelectionTarget != null) previousSelectionTarget.setBackground(null);
            cell.setBackground(FOCUSED_BACKGROUND);
            appView.openChat(cell.getChat());
            previousSelectionTarget = cell;
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

    public static String toCSSColor(Color color) {
        return "#" + color.toString().replaceFirst("0x", "");
    }

}
