package client.gui.customComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class ToolHBox extends HBox {
    public ToolHBox() {
        super();
        setPadding(new Insets(4));
        setSpacing(6);
        setAlignment(Pos.CENTER_LEFT);
    }
}
