package client.gui.customComponents;


import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import client.gui.customComponents.borderless.BorderlessScene;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDateTime;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
*/
public class ChatView extends VBox {

    private Chat chat;
    private Client client;
    private AppView appView;

    private static final CornerRadii DEFAULT_CORNER_RADII = new CornerRadii(6);
    private static final Background DEFAULT = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.desaturate().desaturate().brighter(), CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background DEFAULT_ROUNDED = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR, DEFAULT_CORNER_RADII, Insets.EMPTY));
    private static final Background TEXT_FROM_CLIENT = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.saturate().darker(), DEFAULT_CORNER_RADII, Insets.EMPTY));

    public final ChatMessagesView chatMessagesView;
    private final ColorPicker userColorPicker;

    public ChatView(Chat chat, Client client, AppView appView, BorderlessScene scene) {

        setChat(chat);
        setClient(client);
        setAppView(appView);

        getChat().setChatView(this);

        setStyle("-fx-border-style: solid inside;" +
                 "-fx-border-color: lightgrey;");

        ToolBar chatToolBar = new ToolBar();
        chatToolBar.setMinHeight(AppView.TOOL_BAR_HEIGHT);
        scene.setMoveControl(chatToolBar);
        getChildren().add(chatToolBar);

        Rectangle rectangle = new Rectangle(60d / 2d, 60d / 1.3d);

        StackPane stack = new StackPane();
        stack.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
        chatToolBar.getItems().add(stack);

        userColorPicker = new ColorPicker();
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

        HBox writeMessageRoot = new HBox();
        writeMessageRoot.setSpacing(2);
        writeMessageRoot.setAlignment(Pos.CENTER_LEFT);
        writeMessageRoot.setFillHeight(true);
        getChildren().add(writeMessageRoot);

        WriteMessageTextArea writeMessageTextArea = new WriteMessageTextArea() {
            @Override
            public void onEnter() {
                appView.getController().sendMessage(this, chat.getUserName(), chat);
            }
        };

        writeMessageTextArea.setPrefWidth(42069);
        writeMessageRoot.getChildren().add(writeMessageTextArea);
        writeMessageRoot.minHeightProperty().bind(writeMessageTextArea.minHeightProperty());

        Button sendMessageButton = new Button("Send");
        sendMessageButton.setMinWidth(70);
        sendMessageButton.setOnAction(e -> {
            appView.getController().sendMessage(writeMessageTextArea, chat.getUserName(), chat);
        });
        writeMessageRoot.getChildren().add(sendMessageButton);

        getChildren().add(AppView.slimSeparator());
    }

    private class ChatMessagesView extends ScrollPane {

        private Message lastBuiltMessage; //not last sent message

        public ChatMessagesView() {
            super();

            setPrefHeight(42060);
            setFitToWidth(true);

            VBox root = new VBox();
            root.setAlignment(Pos.TOP_CENTER);
            root.setSpacing(2);
            root.setPadding(new Insets(2, 0, 10, 0));
            setContent(root);

            chat.getMessages().addListener((ListChangeListener<Message>) c -> {
                c.next();
                for(Message message : c.getAddedSubList()) {
                    buildMessage(message, root);
                }
            });

            for (Message message : getChat().getMessages()) {
                buildMessage(message, root);
            }
        }

        private void buildMessage(Message message, VBox root) {
            LocalDateTime t2 = message.getTimeSend();
            boolean dateStampAdded = false;
            if(lastBuiltMessage != null) {
                LocalDateTime t1 = lastBuiltMessage.getTimeSend();
                if(t1.getDayOfYear() != t2.getDayOfYear() && t1.getYear() == t2.getYear()) {
                    Label label = new Label(t2.format(AppView.DAY_MONTH_YEAR));
                    label.setBackground(DEFAULT_ROUNDED);
                    root.getChildren().add(label);
                    dateStampAdded = true;
                }
            } else {
                Label label = new Label(t2.format(AppView.DAY_MONTH_YEAR));
                label.setBackground(DEFAULT_ROUNDED);
                root.getChildren().add(label);
            }

            if(!dateStampAdded && lastBuiltMessage != null) {
                if(!lastBuiltMessage.getFrom().equals(message.getFrom())
                        || lastBuiltMessage.getTimeSend().isBefore(message.getTimeSend().minusHours(2))) {
                    root.getChildren().add(AppView.defaultSeparator());
                }
            }

            HBox cell = new HBox();
            cell.setSpacing(2);
            root.getChildren().add(cell);

            Label time = new Label(message.getTimeSend().format(AppView.HOUR_MINUTE));
            int fontSize = 12;
            time.setStyle("-fx-font-size: " + fontSize);
            time.setMinWidth(fontSize * 2.5);

            GrowingTextArea textArea = new GrowingTextArea(message.getText());
            textArea.maxWidthProperty().bind(cell.widthProperty());
            textArea.hoverProperty().addListener(e -> {
                if(textArea.isHover()) cell.setBackground(DEFAULT);
                else cell.setBackground(null);
            });


            if(message.getFrom().equals(client.getName())) {
                //if sender == this user: right
                textArea.setPadding(new Insets(0, 5, 0, 2));
                textArea.setBackground(TEXT_FROM_CLIENT);

                cell.setAlignment(Pos.CENTER_RIGHT);
                cell.widthProperty().addListener(e -> {
                    cell.setPadding(new Insets(0, 3, 0, getWidth() * 0.2));
                });
                cell.getChildren().addAll(textArea, time); //left
            } else {
                //left
                textArea.setPadding(new Insets(0, 2, 0, 5));
                userColorPicker.valueProperty().addListener(e -> {
                    textArea.setBackground(getHSApprBackground(userColorPicker.getValue()));
                });
                textArea.setBackground(getHSApprBackground(getChat().getColor()));

                cell.setAlignment(Pos.CENTER_LEFT);
                cell.widthProperty().addListener(e -> {
                    cell.setPadding(new Insets(0, getWidth() * 0.2, 0, 3));
                });
                cell.getChildren().addAll(time, textArea); //right
            }

            lastBuiltMessage = message;
        }

        private Background getHSApprBackground(Color color) {
            Color approximationColor = AppView.SLIGHT_HIGHLIGHT_COLOR.saturate().darker();
            while(color.getBrightness() < approximationColor.getBrightness() - 0.2) color = color.brighter();
            while(color.getBrightness() > approximationColor.getBrightness() + 0.2) color = color.darker();
            if(color.getSaturation() > 0) {
                while(color.getSaturation() < approximationColor.getSaturation() - 0.1) color = color.saturate();
                while(color.getSaturation() > approximationColor.getSaturation() + 0.1) color = color.desaturate();
            }

            return new Background(new BackgroundFill(color, DEFAULT_CORNER_RADII, Insets.EMPTY));
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
            setPromptText("[Empty Message]");
            setPrefWidth(42069);
            focusedProperty().addListener(e -> setFocused(false));
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();

            //Call only once per instance!
            if(!layoutDone) callWithLayout();
        }

        private void callWithLayout() {
            //Getting text instance
            ScrollPane scrollPane = (ScrollPane) lookup(".scroll-pane");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
            Region content = (Region) viewport.lookup(".content");
            content.setPadding(new Insets(0));
            text = (Text) content.lookup(".text");

            //Width
            setWrapText(false);
            double textWidth = text.getBoundsInLocal().getWidth() + 10;

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

            layoutDone = true;
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
