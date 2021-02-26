package client.data;

import client.data.cipher.Cipher;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/*
Instances of this class are created, filled with all the data which is supposed to be stored, then they are stored in
a file and later read and re-applied. This is a centralized way of storing everything in one object so that you only
store on object in one file. This makes errors when parsing unlikely and is a good overview to see what is actually
being saved and what isn't.
 */
public class ClientSave implements Serializable {
    private static final long serialVersionUID = 3727499265381891431L;

    public String username;
    public ArrayList<Cipher> chatCiphers = new ArrayList<>();
    public ArrayList<ArrayList<Message>> chatMessages = new ArrayList<>();
    public ArrayList<String> chatUsernames = new ArrayList<>();
    public ArrayList<double[]> colors = new ArrayList<>();
    public ArrayList<LocalDateTime> creationTimes = new ArrayList<>();

    public ClientSave(Client client) {
        username = client.getName();

        ObservableList<Chat> chatsSortedByDate = client.getChats().sorted((c1, c2) -> {
            if(c1.getLastMessage() == null) {
                if(c2.getLastMessage() == null) return c1.getCreationTime().compareTo(c2.getCreationTime());
                else return c1.getCreationTime().compareTo(c2.getLastMessage().getTimeSend());
            } else {
                if(c2.getLastMessage() == null) return c1.getLastMessage().getTimeSend().compareTo(c2.getCreationTime());
                else return c1.getLastMessage().getTimeSend().compareTo(c2.getLastMessage().getTimeSend());
            }
        });

        for(Chat chat : chatsSortedByDate) { //for loop opener
            chatCiphers.add(chat.getCipher());
            creationTimes.add(chat.getCreationTime());
            chatMessages.add(new ArrayList<>(chat.getMessages()));
            chatUsernames.add(chat.getUsername());
            colors.add(new double[] {chat.getColor().getRed(), chat.getColor().getGreen(), chat.getColor().getBlue(), chat.getColor().getOpacity()});
        }
    }

    public void clientOpen(Client client) {
        for(int i = 0; i < chatMessages.size(); i++) {
            Chat chat = new Chat(chatUsernames.get(i));
            chat.setCipher(chatCiphers.get(i));
            chat.setCreationTime(creationTimes.get(i));
            chat.getMessages().addAll(chatMessages.get(i));
            chat.setColor(new Color(colors.get(i)[0], colors.get(i)[1], colors.get(i)[2], colors.get(i)[3]));
            client.getChats().add(chat);
        }
    }
}
