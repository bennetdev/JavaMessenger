package client.gui.customComponents;

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

/*
Instances of this class represent the TextField in which you type your messages. It implements the behavior of (Tab and
Enter) vs ((Shift/Ctrl) + Enter and Ctrl + Tab) as well as the growing vertically to fit more of the message in it. It
also provides a sort-of interface (public void onEnter()) to be overridden so that it can be called internally without
having to pass in the EventHandler.
 */
public class WriteMessageTextArea extends TextArea {
    final TextArea myTextArea = this;
    private final Node nextFocusCycleElement, previousFocusCycleElement;

    public WriteMessageTextArea(Node nextFocusCycleElement, Node previousFocusCycleElement) {
        this.nextFocusCycleElement = nextFocusCycleElement;
        this.previousFocusCycleElement = previousFocusCycleElement;
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
                        if (event.isControlDown()) {
                            recodedEvent = recodeWithoutCtrlAndShiftDown(event);
                            myTextArea.fireEvent(recodedEvent);
                        } else {
                            /*
                             This is super ugly but there is currently no api for focus management. Updates will be done
                             when this JavaFX bug is fixed: https://bugs.openjdk.java.net/browse/JDK-8090456.
                             */
                            if(!event.isShiftDown()) nextFocusCycleElement.requestFocus();
                            else previousFocusCycleElement.requestFocus();
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
