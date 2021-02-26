package client.gui.customComponents;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
This class is a custom node displaying a red or green circular light, similar to how an led would signify an error or
success on an analog device. In this application, I am using it to display if the client and / or their chat partners
are online.
 */
public class ConnectedIcon extends StackPane {

    private final Circle CONNECTED_CIRCLE = new Circle(4);
    private final Color GREEN = new Color(0.2, 1, 0.2, 1);
    private final Color RED = new Color(1, 0.35, 0.35, 1);
    private final Circle BORDER =  new Circle(CONNECTED_CIRCLE.getRadius() + 1, new Color(0.5, 0.5, 0.5, 1));

    public ConnectedIcon() {
        setConnected(false, null);
        getChildren().addAll(BORDER, CONNECTED_CIRCLE);
    }

    public ConnectedIcon(boolean connected, String errorMessage) {
        Circle border = new Circle(CONNECTED_CIRCLE.getRadius() + 1, new Color(0.5, 0.5, 0.5, 1));
        setConnected(connected, errorMessage);
        getChildren().addAll(BORDER, CONNECTED_CIRCLE);
    }

    public void setConnected(boolean connected, String errorMessage) {
        if(connected) {
            CONNECTED_CIRCLE.setFill(GREEN);
            Tooltip.install(this, null);
        } else {
            CONNECTED_CIRCLE.setFill(RED);
            Tooltip.install(this, new Tooltip(errorMessage));
        }
    }
}
