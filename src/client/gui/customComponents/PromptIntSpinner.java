package client.gui.customComponents;

import client.data.cipher.Cipher;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionAdapter;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
I am especially proud of this class. It is used multiple times to select integers or prime values within the Popup of
Encryption settings. It uses raw mouse input and a robot to enable the user to drag to change values and it also
is more responsive with committing textField text changes to values.
 */
public class PromptIntSpinner extends Spinner<Integer> {

    // These two don't have to be final, changing them on the fly is just not yet well enough implemented or needed
    private final String prompt;
    private final boolean prime;

    private int startX, startY;
    private static Robot robot;
    private boolean registeredCommitListener, commitToBeDone;

    public PromptIntSpinner(String prompt, boolean prime) {
        this.prime = prime;
        this.prompt = prompt;
        setValueFactory(new PromptIntSpinnerValueFactory());
        setEditable(true);

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
            robot = new Robot();
        } catch (NativeHookException | AWTException e) {
            e.printStackTrace();
        }
        NativeMouseMotionAdapter listener = new NativeMouseMotionAdapter(){
            @Override
            public void nativeMouseDragged(NativeMouseEvent e) {
                if(e.getX() != startX || e.getY() != startY) {
                    robot.mouseMove(startX, startY);
                    if(e.getY() != startY) {
                        if(e.getY() - startY < 0) Platform.runLater(() -> increment(startY - e.getY()));
                        else Platform.runLater(() -> decrement(e.getY() - startY));
                    }
                }
            }
        };
        setOnDragDetected(e -> {
            GlobalScreen.addNativeMouseMotionListener(listener);
            startX = (int) e.getScreenX();
            startY = (int) e.getScreenY();
        });
        setOnMouseReleased(e -> GlobalScreen.removeNativeMouseMotionListener(listener));


        InvalidationListener commitAction = e -> {
            if(commitToBeDone) {
                increment(0);
                getEditor().positionCaret(getEditor().getText().length());
            }
            commitToBeDone = false;
        };

        getEditor().setOnAction(e -> {
            increment(1);
            decrement(1);
            getEditor().positionCaret(getEditor().getText().length());
        });

        focusedProperty().addListener(commitAction);

        NativeMouseMotionAdapter commitListener = new NativeMouseMotionAdapter(){
            @Override
            public void nativeMouseMoved(NativeMouseEvent e) {
                GlobalScreen.removeNativeMouseMotionListener(this);
                registeredCommitListener = false;
                Platform.runLater(() -> commitAction.invalidated(null));
            }
        };

        getEditor().textProperty().addListener((source, oldV, newV) -> {
            if(!getValueFactory().getValue().equals(getValueFactory().getConverter().fromString(newV)) && !oldV.replaceAll("[^0-9]", "").equals(newV.replaceAll("[^0-9]", ""))) commitToBeDone = true;
            else return;
            if(!registeredCommitListener) GlobalScreen.addNativeMouseMotionListener(commitListener);
            registeredCommitListener = true;
        });

    }

    /*
    Use this instead of getValue() if prime == true
     */
    public long getVal() {
        if(prime) return Cipher.getNthPrime(getValue());
        else return getValue();
    }

    /*
    Use this instead of setValue() if prime == true
     */
    public void setVal(long val) {
        if(prime) getValueFactory().setValue(Cipher.indexOfPrime(val));
        else getValueFactory().setValue((int) val);
    }

    private class PromptIntSpinnerValueFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {
        public PromptIntSpinnerValueFactory() {
            super(0, Integer.MAX_VALUE, 0);

            setConverter(new StringConverter<Integer>() {
                @Override
                public String toString(Integer value) {
                    if(prime) return prompt == null ? Cipher.getNthPrime(value).toString() : prompt + Cipher.getNthPrime(value);
                    else return prompt == null ? value.toString() : prompt + value;
                }

                @Override
                public Integer fromString(String text) {
                    try {
                        long val = Long.parseLong(text.replaceAll("[^0-9]", ""));
                        if(prime) {
                            return Cipher.isPrime(val) ? Cipher.indexOfPrime(val) : Cipher.indexOfPrime(Cipher.nextPrime(val));
                        } else return (int) val;
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
        }
    }
}
