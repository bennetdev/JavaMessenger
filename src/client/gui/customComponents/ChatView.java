package client.gui.customComponents;


import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import client.gui.customComponents.borderless.BorderlessScene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
*/
public class ChatView extends VBox {

    private Chat chat;
    private Client client;
    private AppView appView;


    //ChatNavigationList.focusedBackground
    private static final Background DEFAULT = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.desaturate().desaturate().brighter(), CornerRadii.EMPTY, Insets.EMPTY));
    public final ChatMessagesView chatMessagesView;

    public ChatView(Chat chat, Client client, AppView appView, BorderlessScene scene) {

        setChat(chat);
        setClient(client);
        setAppView(appView);

        setStyle("-fx-border-style: solid inside;" +
                 "-fx-border-color: lightgrey;");

        ToolBar chatToolBar = new ToolBar();
        chatToolBar.setMinHeight(AppView.TOOL_BAR_HEIGHT);
        scene.setMoveControl(chatToolBar);
        getChildren().add(chatToolBar);

        getChildren().add(AppView.defaultSeparator());

        Rectangle rectangle = new Rectangle(60d / 2d, 60d / 1.3d);

        StackPane stack = new StackPane();
        stack.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
        chatToolBar.getItems().add(stack);

        ColorPicker userColorPicker = new ColorPicker();
        userColorPicker.setFocusTraversable(false);
        userColorPicker.setMinHeight(rectangle.getHeight());
        userColorPicker.valueProperty().bindBidirectional(chat.getColorProperty());
        stack.getChildren().addAll(userColorPicker, rectangle);

        rectangle.setMouseTransparent(true); //Mouse clicks the ColorPicker, not the Rectangle.
        rectangle.setFill(chat.getColor());
        rectangle.fillProperty().bind(userColorPicker.valueProperty());

        Label name = new Label(chat.getUserName());
        name.textFillProperty().bind(userColorPicker.valueProperty());
        name.setStyle("-fx-font-weight: bold;");
        chatToolBar.getItems().add(name);

        chatMessagesView = new ChatMessagesView();
        getChildren().add(chatMessagesView);
    }


    private class ChatMessagesView extends ScrollPane {

        public ChatMessagesView() {
            super();

            setPrefHeight(42060);
            setFitToWidth(true);

            VBox root = new VBox();

            setContent(root);

            for (Message message : getChat().getMessages()) {
                HBox cell = new HBox(getChat());
//            cell.hoverProperty().addListener(e -> {
//                cell.setBackground(focusedBackground);
//            });

                cell.setBackground(DEFAULT);
                cell.setPadding(new Insets(3, 3, 3, 3));
                cell.setSpacing(8);
                root.getChildren().add(cell);

                GrowingTextArea textArea = new GrowingTextArea(message.getText());
                textArea.maxWidthProperty().bind(cell.widthProperty());
                cell.getChildren().add(textArea);
            }

        }
    }

    public class GrowingTextArea extends TextArea {

        private static final double DEFAULT_WIDTH = 40, DEFAULT_HEIGHT = 20;
        private boolean layoutDone = false;
        private Text text;

        public GrowingTextArea(String text) {
            super(text);
            setWrapText(true);
            setEditable(false);
            setFocusTraversable(false);
            focusedProperty().addListener(e -> {
                if(!isFocused()) deselect();
            });
            setPromptText("[Empty Message]");
            setPadding(new Insets(0, 2, 0, 5));
            setPrefWidth(42069);
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();

            //Call only once per instance!
            if(!layoutDone) callWithLayout();
        }

        private void callWithLayout() {
            layoutDone = true;
            ScrollPane scrollPane = (ScrollPane) lookup(".scroll-pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            StackPane viewport = (StackPane) scrollPane.lookup(".viewport");

            Region content = (Region) viewport.lookup(".content");
            content.setPadding(new Insets(0));

            text = (Text) content.lookup(".text");

            //Width
            setWrapText(false);
            double textWidth = text.getBoundsInLocal().getWidth() + 9;

            if (textWidth < DEFAULT_WIDTH) {
                textWidth = DEFAULT_WIDTH;
            }
            setPrefWidth(textWidth);
            setWrapText(true);


            //Height
            double textHeight = text.getBoundsInLocal().getHeight() + 2;

            if (textHeight < DEFAULT_HEIGHT) {
                textHeight = DEFAULT_HEIGHT;
            }

            setPrefHeight(textHeight);

            maxWidthProperty().addListener(e -> {
                double textHeightl = this.text.getBoundsInLocal().getHeight() + 2;
                if (textHeightl < DEFAULT_HEIGHT) textHeightl = DEFAULT_HEIGHT;
                setMinHeight(textHeightl);
                setPrefHeight(textHeightl);
                setMaxHeight(textHeightl);
            });
        }
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AppView getAppView() {
        return appView;
    }

    public void setAppView(AppView appView) {
        this.appView = appView;
    }
}
