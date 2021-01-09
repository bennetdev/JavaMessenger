package client.gui.customComponents;

import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
 */
public class ChatNavigationList extends ScrollPane {

    private ChatHBox previousSelectionTarget;
    public static final Background FOCUSED_BACKGROUND =
            new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR, CornerRadii.EMPTY, Insets.EMPTY));

    public ChatNavigationList(Client client, AppView appView) {
        super();

        setPrefHeight(42060);
        setFitToWidth(true);

        VBox root = new VBox();
        root.setPrefHeight(600);

        setContent(root);

        for(Chat chat : client.getChats()) {
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
            });
            nameSplitMessage.getChildren().add(lastMessage);

            root.getChildren().add(cell);
        }
    }

    public static String toCSSColor(Color color) {
        return "#" + color.toString().replaceFirst("0x", "");
    }

}
