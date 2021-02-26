package client.data;

import java.io.Serializable;

/*
This tiny class's instances are only used for Server-Client communication to provide a one-object transfer for two
types of data: String (username) and boolean (connected). It is sent, everytime one of your chat partners connects
or disconnects. This class could also be in the server package, but I decided against it to make exporting the
application more convenient.
 */
public class ConnectedInfo implements Serializable {
    public static final long serialVersionUID = 4206900001093226943L;

    private final boolean connected;
    private final String username;

    public ConnectedInfo(boolean connected, String username) {
        this.connected = connected;
        this.username = username;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "user: " + getUsername() + "   online: " + isConnected();
    }
}
