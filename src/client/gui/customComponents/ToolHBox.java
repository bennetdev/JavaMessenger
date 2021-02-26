package client.gui.customComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/*
Just a tiny class which objects are used many times. This just saves some code and could be replaced by a static method
returning an appropriate instance. Not sure if I want to add more in the future, though, so I want to keep the
possibility
 */

public class ToolHBox extends HBox {
    public ToolHBox() {
        super();
        setPadding(new Insets(4));
        setSpacing(6);
        setAlignment(Pos.CENTER_LEFT);
    }
}
