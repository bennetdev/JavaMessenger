package client.gui.customComponents;

import client.gui.AppView;
import client.gui.customComponents.borderless.BorderlessScene;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ChatToolBar extends HBox {

    private final ToolHBox chatToolBar;

    public ChatToolBar(AppView appView, BorderlessScene scene) {
        setMinHeight(AppView.TOOL_BAR_HEIGHT);
        styleProperty().bind(appView.navToolBar.styleProperty());
        scene.setMoveControl(this);

        chatToolBar = new ToolHBox();
        chatToolBar.setMinWidth(0);
        chatToolBar.setPrefWidth(42069);
        getChildren().add(chatToolBar);

        HBox windowControlBox = new HBox();
        windowControlBox.setAlignment(Pos.TOP_RIGHT);
        windowControlBox.setMinWidth(130);
        windowControlBox.setMaxHeight(30);
        windowControlBox.setStyle("-fx-background-image: url(\"client/gui/resources/windowControlBackground.png\") no-repeat center center fixed;");
        getChildren().add(windowControlBox);

        Button minimize = new Button();
        minimize.setFocusTraversable(false);
        minimize.setGraphic(new ImageView(AppView.RESOURCES + "minimize.png"));
        minimize.getStyleClass().add("window-control-normal");
        windowControlBox.getChildren().add(minimize);
        minimize.setOnAction(e -> scene.minimizeStage());

        Button maximize = new Button();
        maximize.setFocusTraversable(false);
        maximize.setGraphic(new ImageView(AppView.RESOURCES + "maximize.png"));
        maximize.getStyleClass().add("window-control-normal");
        windowControlBox.getChildren().add(maximize);
        maximize.setOnAction(e -> scene.maximizeStage());

        Button close = new Button();
        close.setFocusTraversable(false);
        close.setGraphic(new ImageView(AppView.RESOURCES + "closeBlack.png"));
        close.getStyleClass().add("window-control-close");
        windowControlBox.getChildren().add(close);
        close.setOnAction(e -> Platform.exit());
    }

    public void addItem(Node node) {
        chatToolBar.getChildren().add(node);
    }
}
