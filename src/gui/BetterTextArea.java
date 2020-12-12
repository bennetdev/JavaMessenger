package gui;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

public class BetterTextArea extends TextArea {
    final TextArea myTextArea = this;

    public BetterTextArea() {
        addEventFilter(KeyEvent.KEY_PRESSED, new BetterHandler());
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
                        if (event.isControlDown()) {
                            recodedEvent = recodeWithoutControlDown(event);
                            myTextArea.fireEvent(recodedEvent);
                        } else {
                            onEnter();
                            Event parentEvent = event.copyFor(parent, parent);
                            myTextArea.getParent().fireEvent(parentEvent);
                        }
                        event.consume();
                        break;

                    case TAB:
                        if (event.isControlDown()) {
                            recodedEvent = recodeWithoutControlDown(event);
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

    public void onEnter() {
        //To be overridden
    }

    private KeyEvent recodeWithoutControlDown(KeyEvent event) {
        return new KeyEvent(
                event.getEventType(),
                event.getCharacter(),
                event.getText(),
                event.getCode(),
                event.isShiftDown(),
                false,
                event.isAltDown(),
                event.isMetaDown()
        );
    }
}
