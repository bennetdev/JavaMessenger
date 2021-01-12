package client.data;

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientSave implements Serializable {

    public String username;
    public ArrayList<ArrayList<Message>> chatsMessages = new ArrayList<>();
    public ArrayList<String> usernames = new ArrayList<>();
    public ArrayList<double[]> colors = new ArrayList<>();

    public ClientSave(Client client) {
        username = client.getName();
        for(Chat chat : client.getChats().sorted((c1, c2) -> {
            if(c1.getLastMessage() == null) {
                if(c2.getLastMessage() == null) return c1.getCreationTime().compareTo(c2.getCreationTime());
                else return c1.getCreationTime().compareTo(c2.getLastMessage().getTimeSend());
            } else {
                if(c2.getLastMessage() == null) return c1.getLastMessage().getTimeSend().compareTo(c2.getCreationTime());
                else return c1.getLastMessage().getTimeSend().compareTo(c2.getLastMessage().getTimeSend());
            }
        })) {
            ArrayList<Message> messages = new ArrayList<>(chat.getMessages());
            chatsMessages.add(messages);
            usernames.add(chat.getUserName());
            colors.add(new double[] {chat.getColor().getRed(), chat.getColor().getGreen(), chat.getColor().getBlue(), chat.getColor().getOpacity()});
        }
    }

    public void clientOpen(Client client) {
        client.setName(username);
        for(int i = 0; i < chatsMessages.size(); i++) {
            Chat chat = new Chat(usernames.get(i));
            chat.getMessages().addAll(chatsMessages.get(i));
            chat.setColor(new Color(colors.get(i)[0], colors.get(i)[1], colors.get(i)[2], colors.get(i)[3]));
            client.getChats().add(chat);
        }
    }
}
