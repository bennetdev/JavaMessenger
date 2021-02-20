package client.gui.customComponents;


import client.data.Chat;
import client.data.Client;
import client.data.Message;
import client.gui.AppView;
import client.gui.EncryptionSettingsStage;
import client.gui.Main;
import client.gui.customComponents.borderless.BorderlessScene;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/*
Used to represent minimally a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
*/
public class ChatView extends VBox {

    private Chat chat;
    private Client client;
    private AppView appView;
    private Message.EncryptionMethod encryptionMethod;
    private EncryptionSettingsStage encryptionSettingsStage;

    public final ChatMessagesView chatMessagesView;
    public InvalidationListener lastMaxWidthListener;
    
    private final ColorPicker userColorPicker;
    private final WriteMessageTextArea writeMessageTextArea;

    public ChatView(Chat chat, Client client, AppView appView, BorderlessScene scene) {

        setChat(chat);
        setClient(client);
        setAppView(appView);
        setMinSize(0, 0);

        getChat().setChatView(this);

        ChatToolBar chatToolBar = new ChatToolBar(appView, scene);
        getChildren().add(chatToolBar);

        Rectangle rectangle = new Rectangle(60d / 2d, 60d / 1.3d);

        StackPane stack = new StackPane();
        stack.setMaxSize(rectangle.getWidth(), rectangle.getHeight());
        chatToolBar.addItem(stack);

        userColorPicker = new ColorPicker();
        userColorPicker.setFocusTraversable(false);
        userColorPicker.setMinHeight(rectangle.getHeight());
        userColorPicker.valueProperty().bindBidirectional(chat.getColorProperty());
        stack.getChildren().addAll(userColorPicker, rectangle);

        rectangle.setMouseTransparent(true); //Mouse clicks the ColorPicker, not the Rectangle.
        rectangle.fillProperty().bind(userColorPicker.valueProperty());

        Label name = new Label(chat.getUserName());
        name.setMinWidth(Region.USE_PREF_SIZE);
        name.textFillProperty().bind(userColorPicker.valueProperty());
        name.setStyle("-fx-font-weight: bold;");
        chatToolBar.addItem(name);

        HBox encryptionGroupContainer = new HBox();
        encryptionGroupContainer.setPadding(new Insets(0, 0, 0, 15));
        encryptionGroupContainer.setAlignment(Pos.BOTTOM_RIGHT);
        chatToolBar.addItem(encryptionGroupContainer);

        encryptionSettingsStage = new EncryptionSettingsStage(client.getCipher());

        Button encryptionSettingsButton = new Button();
        encryptionSettingsButton.setTooltip(new Tooltip("Change encryption settings"));
        encryptionSettingsButton.setPrefHeight(30);
        encryptionSettingsButton.setGraphic(new ImageView(AppView.RESOURCES + "settings.png"));
        encryptionSettingsButton.setOnAction(ae -> {
            encryptionSettingsStage.open(encryptionMethod);
        });
        encryptionGroupContainer.getChildren().add(encryptionSettingsButton);

        ComboBox<Message.EncryptionMethod> encryptionChooser = new ComboBox<>();
        encryptionChooser.setTooltip(new Tooltip("Type of end to end encryption to use for incoming and outgoing" +
                " messages\nMust be the encryption " + chat.getUserName() + " is using."));
        encryptionChooser.getItems().addAll(Message.EncryptionMethod.values());
        encryptionChooser.valueProperty().addListener(e -> {
            encryptionMethod = encryptionChooser.getValue();
            encryptionSettingsButton.setDisable(encryptionMethod == Message.EncryptionMethod.NOT_ENCRYPTED);
        });
        encryptionChooser.getSelectionModel().select(0);
        encryptionGroupContainer.getChildren().add(encryptionChooser);

        chatMessagesView = new ChatMessagesView(new VBox());
        getChildren().add(chatMessagesView);

        HBox writeMessageRoot = new HBox();
        writeMessageRoot.setSpacing(2);
        writeMessageRoot.setAlignment(Pos.CENTER_LEFT);
        writeMessageRoot.setFillHeight(true);
        getChildren().add(writeMessageRoot);

        writeMessageTextArea = new WriteMessageTextArea() {
            @Override
            public void onEnter() {
                appView.getController().sendMessage(this, chat.getUserName(), chat);
                layoutChildrenShortcut();
            }
        };
        getWriteMessageTextArea().setPrefWidth(42069);
        writeMessageRoot.getChildren().add(getWriteMessageTextArea());
        writeMessageRoot.minHeightProperty().bind(getWriteMessageTextArea().minHeightProperty());

        Button sendMessageButton = new Button("Send");
        sendMessageButton.setMinWidth(70);
        sendMessageButton.setOnAction(e -> {
            appView.getController().sendMessage(getWriteMessageTextArea(), chat.getUserName(), chat);
        });
        writeMessageRoot.getChildren().add(sendMessageButton);

        getChildren().add(AppView.slimSeparator());
    }


    /*

     */
    public class ChatMessagesView extends SmoothScrollPane {

        private final CornerRadii DEFAULT_CORNER_RADII = new CornerRadii(6);
        private final Background
                DEFAULT = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.desaturate().desaturate().brighter(), CornerRadii.EMPTY, Insets.EMPTY)),
                DEFAULT_ROUNDED = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR, DEFAULT_CORNER_RADII, Insets.EMPTY)),
                TEXT_FROM_CLIENT = new Background(new BackgroundFill(AppView.SLIGHT_HIGHLIGHT_COLOR.saturate().darker(), DEFAULT_CORNER_RADII, Insets.EMPTY));
        private final SimpleObjectProperty<Background> textFromOtherUser = new SimpleObjectProperty<>();

        private ScrollBar vScrollBar;

        private Message lastBuiltMessage; //not last sent message
        private final ChatMessagesView scrollPane = this;
        private int vv1 = 0;

        private static final int CHUNK_LOADING_SIZE = 10;
        private int oldestLoadedMessageIndex;
        private boolean dynamicLoadingOnCooldown;

        @Override
        public void layoutChildren() {
            super.layoutChildren();
            if(vv1 > 0) {
                setVvalue(1);
                vv1++;
                if(vv1 > 3) vv1 = 0;
            }
        }

        public ChatMessagesView(VBox content) {
            super(content);

            setPrefHeight(42060);
            setFitToWidth(true);
            setStyle("-fx-background-color:transparent;");

            content.setAlignment(Pos.TOP_CENTER);
            content.setSpacing(2);
            content.setPadding(new Insets(2, 0, 10, 0));
            setContent(content);

            userColorPicker.valueProperty().addListener(e -> {
                textFromOtherUser.setValue(getHSApprBackground(userColorPicker.getValue()));
            });
            textFromOtherUser.setValue(getHSApprBackground(userColorPicker.getValue()));

            chat.getMessages().addListener((ListChangeListener<Message>) c -> {
                c.next();
                    for(Message message : c.getAddedSubList()) {
                        buildMessage(message, content, null);
                        if(lastMaxWidthListener != null) lastMaxWidthListener.invalidated(null);
                    }
                vv1 = 1;
            });

            //Load last CHUNK_LOADING_SIZE(10) Messages
            ObservableList<Message> messages = getChat().getMessages();
            int i;
            if(messages.size() > CHUNK_LOADING_SIZE) i = messages.size() - 1 - CHUNK_LOADING_SIZE;
            else i = 0;
            oldestLoadedMessageIndex = i;
            for (; i < messages.size(); i++) {
                buildMessage(messages.get(i), content, null);
            }

            //Dynamically loads next CHUNK_LOADING_SIZE(10) Messages
            final InvalidationListener loadMoreMessagesListener = e -> {
                if((getVvalue() == 0 || (vScrollBar != null && !vScrollBar.isVisible()))
                        && oldestLoadedMessageIndex != 0 && !dynamicLoadingOnCooldown) {
                    dynamicLoadingOnCooldown = true;

                    final Node oldLastNode = content.getChildren().get(0);

                    int j = Math.max(0, oldestLoadedMessageIndex - CHUNK_LOADING_SIZE);
                    int temp = j;

                    ArrayList<Node> toBeAdded = new ArrayList<>();
                    for (; j < oldestLoadedMessageIndex; j++) {
                        buildMessage(messages.get(j), content, toBeAdded);
                    }
                    content.getChildren().addAll(0, toBeAdded);

                    oldestLoadedMessageIndex = temp;
                    Main.executor.schedule(() ->
                        setVvalue(oldLastNode.getLayoutY() * (1 / (content.getHeight() - getViewportBounds().getHeight())))
                    , 200, TimeUnit.MILLISECONDS);
                    Main.executor.schedule(() -> dynamicLoadingOnCooldown = false, 300, TimeUnit.MILLISECONDS);
                }
            };

            Platform.runLater(() -> {
                vvalueProperty().addListener(loadMoreMessagesListener);
                setOnScroll(e -> loadMoreMessagesListener.invalidated(vvalueProperty()));

                vScrollBar = getVerticalScrollbar();
                if(vScrollBar == null) {
                    Main.executor.schedule(() -> {
                        vScrollBar = getVerticalScrollbar();
                        vScrollBar.visibleProperty().addListener(loadMoreMessagesListener);
                    }, 200, TimeUnit.MILLISECONDS);
                } else vScrollBar.visibleProperty().addListener(loadMoreMessagesListener);
            });

            vv1 = 1;
        }

        private void buildMessage(Message message, VBox root, ArrayList<Node> toBeAdded) {
            LocalDateTime t2 = message.getTimeSend();
            boolean dateStampAdded = false;
            if(lastBuiltMessage != null) {
                LocalDateTime t1 = lastBuiltMessage.getTimeSend();
                if(t1.getDayOfYear() != t2.getDayOfYear() && t1.getYear() == t2.getYear()) dateStampAdded = true;
            } else dateStampAdded = true;

            if(dateStampAdded) {
                Label label = new Label(t2.format(AppView.DAY_MONTH_YEAR));
                label.setBackground(DEFAULT_ROUNDED);
                if(toBeAdded == null) {
                    root.getChildren().add(AppView.defaultSeparator());
                    root.getChildren().add(label);
                    root.getChildren().add(AppView.defaultSeparator());
                }
                else {
                    toBeAdded.add(AppView.defaultSeparator());
                    toBeAdded.add(label);
                    toBeAdded.add(AppView.defaultSeparator());
                }
            } else if(lastBuiltMessage != null) {
                if(!lastBuiltMessage.getFrom().equals(message.getFrom())
                        || lastBuiltMessage.getTimeSend().isBefore(message.getTimeSend().minusHours(2))) {
                    if (toBeAdded == null) root.getChildren().add(AppView.defaultSeparator());
                    else toBeAdded.add(AppView.defaultSeparator());
                }
            }



            HBox cell = new HBox();
            cell.setSpacing(2);
            if (toBeAdded == null) root.getChildren().add(cell);
            else toBeAdded.add(cell);

            Label time = new Label(message.getTimeSend().format(AppView.HOUR_MINUTE));
            time.setMouseTransparent(false);
            int fontSize = 12;
            time.setStyle("-fx-font-size: " + fontSize);
            time.setMinWidth(fontSize * 2.5);

            GrowingChatBubble textArea = new GrowingChatBubble(message.getText());

            Platform.runLater(() -> textArea.maxWidthProperty().bind(cell.widthProperty()));
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
                textArea.backgroundProperty().bind(textFromOtherUser);

                cell.setAlignment(Pos.CENTER_LEFT);
                cell.widthProperty().addListener(e -> {
                    cell.setPadding(new Insets(0, getWidth() * 0.2, 0, 3));
                });
                cell.getChildren().addAll(time, textArea); //right
            }

            lastBuiltMessage = message;
        }

        private ScrollBar getVerticalScrollbar() {
            ScrollBar result = null;

            if(getChildren().size() > 0) result = (ScrollBar) getChildren().get(1);
            else {
                for (Node n : lookupAll(".scroll-bar")) {
                    if (n instanceof ScrollBar) {
                        ScrollBar bar = (ScrollBar) n;
                        if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                            result = bar;
                        }
                    }
                }
            }
            return result;
        }

        private Background getHSApprBackground(Color color) {
            Color approximationColor = AppView.CLIENT_COLOR;
            while(color.getBrightness() < approximationColor.getBrightness() - 0.2) color = color.brighter();
            while(color.getBrightness() > approximationColor.getBrightness() + 0.2) color = color.darker();
            if(color.getSaturation() > 0) {
                while(color.getSaturation() < approximationColor.getSaturation() - 0.2) color = color.saturate();
                while(color.getSaturation() > approximationColor.getSaturation() + 0.2) color = color.desaturate();
            }
            return new Background(new BackgroundFill(color, DEFAULT_CORNER_RADII, Insets.EMPTY));
        }


        /*

         */
        public class GrowingChatBubble extends TextArea {
            private static final double DEFAULT_WIDTH = 40, DEFAULT_HEIGHT = 20;
            private boolean layoutDone = false;
            private Text text;

            public GrowingChatBubble(String text) {
                super(text);
                setPrefWidth(69420);
                setPrefHeight(1);

                setEditable(false);
                setFocusTraversable(false);
                setPromptText("[Empty Message]");
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
                try {
                    ScrollPane scrollPane = (ScrollPane) lookup(".scroll-pane");
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                    StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
                    viewport.setBackground(Background.EMPTY);
                    Region content = (Region) viewport.lookup(".content");
                    content.setPadding(new Insets(0));
                    content.setBackground(Background.EMPTY);
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

                    InvalidationListener maxWidthListener = e -> Platform.runLater(() -> {
                        double textHeight3 = this.text.prefHeight(getMaxWidth()) + 2;
                        if (textHeight3 < DEFAULT_HEIGHT) textHeight3 = DEFAULT_HEIGHT;
                        setMinHeight(textHeight3);
                        setPrefHeight(textHeight3);
                        setMaxHeight(textHeight3);
                    });
                    lastMaxWidthListener = maxWidthListener;
                    maxWidthProperty().addListener(maxWidthListener);
                    this.text.wrappingWidthProperty().addListener(maxWidthListener);

                    layoutDone = true;
                } catch (Exception e) {
                    Platform.runLater(this::callWithLayout);
                }
            }
        }
    }

    public WriteMessageTextArea getWriteMessageTextArea() {
        return writeMessageTextArea;
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

    public Message.EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public EncryptionSettingsStage getEncryptionSettingsStage() {
        return encryptionSettingsStage;
    }
}
