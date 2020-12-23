package client.gui.customComponents;

import client.data.Chat;
import client.data.Client;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

/*
Used to represent a chat graphically by using information from private Chat chat
Will be put in a ScrollPane
 */
public class ChatNavigationPane extends ListView<Chat> {

    private Client client;

    public ChatNavigationPane(Client client) {
        super(client.getChats());

        setFixedCellSize(60);


        setCellFactory(new Callback<ListView<Chat>, ListCell<Chat>>() {
            @Override public ListCell<Chat> call(ListView<Chat> p) {
                return new ListCell<Chat>() {
                    private final Rectangle rectangle;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        rectangle = new Rectangle(getFixedCellSize() / 2, getFixedCellSize() / 1.3);
                    }

                    @Override protected void updateItem(Chat item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            rectangle.setFill(item.getColor());
                            setGraphic(rectangle);
                        }
                    }
                };
            }
        });

        for(Chat chat : getItems()) {
            System.out.println(chat);
        }
    }
}
