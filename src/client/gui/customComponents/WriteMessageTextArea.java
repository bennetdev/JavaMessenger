package client.gui.customComponents;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class WriteMessageTextArea extends TextArea {
    final TextArea myTextArea = this;

    public WriteMessageTextArea() {
        addEventFilter(KeyEvent.KEY_PRESSED, new BetterHandler());
        setPrefHeight(5);
        setWrapText(true);
        setFocusTraversable(true);
    }

    class BetterHandler implements EventHandler<KeyEvent> {
        private KeyEvent recodedEvent;

        @Override
        public void handle(KeyEvent event) {
            if (recodedEvent != null) {
                recodedEvent = null;
                return;
            }

            Parent parent = getParent();
            if (parent != null) {
                switch (event.getCode()) {
                    case ENTER:
                        if (event.isControlDown() || event.isShiftDown()) {
                            recodedEvent = recodeWithoutCtrlAndShiftDown(event);
                            myTextArea.fireEvent(recodedEvent);
                        } else {
                            onEnter();
                            Event parentEvent = event.copyFor(parent, parent);
                            myTextArea.getParent().fireEvent(parentEvent);
                        }
                        event.consume();
                        break;

                    case TAB:
                        if (event.isControlDown() || event.isShiftDown()) {
                            recodedEvent = recodeWithoutCtrlAndShiftDown(event);
                            myTextArea.fireEvent(recodedEvent);
                        } else {
                            ObservableList<Node> children = parent.getChildrenUnmodifiable();
                            int idx = children.indexOf(myTextArea);
                            if (idx >= 0) {
                                for (int i = idx + 1; i < children.size(); i++) {
                                    if (children.get(i).isFocusTraversable()) {
                                        children.get(i).requestFocus();
                                        break;
                                    }
                                }
                                for (int i = 0; i < idx; i++) {
                                    if (children.get(i).isFocusTraversable()) {
                                        children.get(i).requestFocus();
                                        break;
                                    }
                                }
                            }
                        }
                        event.consume();
                        break;
                }
            }
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        ScrollPane scrollPane = (ScrollPane) lookup(".scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
        Region content = (Region) viewport.lookup(".content");
        Text text = (Text) content.lookup(".text");

        double textHeight = text.getBoundsInLocal().getHeight() + 10;
        setMinHeight(Math.min(textHeight, myTextArea.getParent().getParent().getBoundsInLocal().getHeight() / 3));
    }

    public void onEnter() {
        //To be overridden
    }

    public void layoutChildrenShortcut() {
        layoutChildren();
    }

    private KeyEvent recodeWithoutCtrlAndShiftDown(KeyEvent event) {
        return new KeyEvent(
                event.getEventType(),
                event.getCharacter(),
                event.getText(),
                event.getCode(),
                false,
                false,
                event.isAltDown(),
                event.isMetaDown()
        );
    }
}
