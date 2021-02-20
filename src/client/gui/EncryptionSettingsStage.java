package client.gui;

import client.data.Message;
import client.data.cipher.Cipher;
import client.gui.customComponents.PromptIntSpinnerValueFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class EncryptionSettingsStage {

    private int caesarKey = -1;
    private int pRSA = -1;
    private int qRSA = -1;
    private int eRSA = -1;
    private String vigenereKey;

    private Cipher cipher;

    private final Scene mono;
    private final Scene poly;
    private final Scene rsa;
    private final Stage popup;

    public EncryptionSettingsStage(Cipher cipher) {
        setCipher(cipher);
        popup = new Stage(StageStyle.TRANSPARENT);
        getPopup().setOnShowing(e -> {
            getPopup().setX(MouseInfo.getPointerInfo().getLocation().getX());
            getPopup().setY(MouseInfo.getPointerInfo().getLocation().getY());
        });

        getPopup().focusedProperty().addListener(e -> {
            if(!getPopup().isFocused()) {
                getPopup().close();
            }
        });

        mono = getMonoScene();
        poly = getPolyScene();
        rsa = getRsaScene();
    }

    public void open(Message.EncryptionMethod encryptionMethod) {
        if(encryptionMethod == Message.EncryptionMethod.CAESAR) {
            getPopup().setScene(getMono());
        } else if(encryptionMethod == Message.EncryptionMethod.VIGENERE) {
            getPopup().setScene(getPoly());
        } else if(encryptionMethod == Message.EncryptionMethod.RSA) {
            getPopup().setScene(getRsa());
        } else {
            return;
        }
        getPopup().show();
    }

    private Scene getMonoScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        Spinner<Integer> keySpinner = new Spinner<>();
        ok.setDefaultButton(true);
        ok.setDisable(true);
        EventHandler<ActionEvent> okEvent = e -> {
            getCipher().getMonoAlphabetic().setKey(keySpinner.getValue());
            getPopup().close();
        };
        ok.setOnAction(okEvent);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        EventHandler<ActionEvent> cancelEvent = e -> getPopup().close();
        cancel.setOnAction(cancelEvent);
        okCancel.getChildren().add(cancel);

        root.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                cancelEvent.handle(null);
                e.consume();
            }
        });

        keySpinner.setValueFactory(new PromptIntSpinnerValueFactory(0, Integer.MAX_VALUE, 0, "Key"));
        keySpinner.setPrefWidth(200);
        keySpinner.setEditable(true);
        keySpinner.setTooltip(new Tooltip("Key (Integer, same as chat partner, neither 0 nor multiple of Cipher.getUtfMaxValue())"));
        keySpinner.valueProperty().addListener(e -> {
            ok.setDisable(keySpinner.getValue() % Cipher.getUtfMaxValue() == 0);
        });
        keySpinner.getEditor().setOnKeyPressed(e -> Platform.runLater(() -> {
            //increment(0) to force an update of value and text
            keySpinner.increment(0);
            keySpinner.getEditor().positionCaret(keySpinner.getEditor().getText().length());
        }));

        root.getChildren().addAll(keySpinner, okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }


    private Scene getPolyScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        TextField keyTextField = new TextField();
        ok.setDefaultButton(true);
        ok.setDisable(true);
        ok.setOnAction(e -> {
            getCipher().getPolyAlphabetic().setKey(keyTextField.getText());
            getPopup().close();
        });
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        EventHandler<ActionEvent> cancelEvent = e -> getPopup().close();
        cancel.setOnAction(cancelEvent);
        okCancel.getChildren().add(cancel);

        root.setOnKeyPressed(e -> {
            if(e.getCode().equals(KeyCode.ESCAPE)) {
                cancelEvent.handle(null);
                e.consume();
            }
        });

        keyTextField.textProperty().addListener(e -> {
            if(keyTextField.getText().trim().equals("")) {
                ok.setDisable(true);
                System.out.println("Key can't be empty and can't be only spaces");
            } else ok.setDisable(false);
        });
        keyTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        keyTextField.setPrefWidth(200);
        keyTextField.setPromptText("Key (String, same as chat partner)");
        root.getChildren().add(keyTextField);

        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }


    @SuppressWarnings("CodeBlock2Expr")
    private Scene getRsaScene() {
        VBox root = new VBox();
        root.setBackground(Background.EMPTY);
        root.setPadding(new Insets(5));
        root.setSpacing(5);

        HBox okCancel = new HBox();
        okCancel.setPadding(new Insets(0, 5, 5, 5));
        okCancel.setSpacing(5);
        okCancel.setAlignment(Pos.BOTTOM_CENTER);

        Button ok = new Button("Ok");
        ok.setDefaultButton(true);
        ok.setDisable(true);
        okCancel.getChildren().add(ok);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> getPopup().close());
        okCancel.getChildren().add(cancel);

        HBox pqBox = new HBox();
        pqBox.setSpacing(5);
        root.getChildren().add(pqBox);

        TextField pTextField = new TextField();
        TextField qTextField = new TextField();
        TextField nTextField = new TextField();
        TextField mTextField = new TextField();
        TextField eTextField = new TextField();
        TextField dTextField = new TextField();

        pTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });
        qTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });
        eTextField.textProperty().addListener(e -> {
            ok.setDisable(checkRSAEntryValidity(pTextField, qTextField, eTextField, nTextField, mTextField, dTextField));
        });

        pTextField.setPrefWidth(100);
        pTextField.setPromptText("p: Prime Integer");
        pTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        pqBox.getChildren().add(pTextField);

        qTextField.setPrefWidth(100);
        qTextField.setPromptText("q: Prime Integer");
        qTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        pqBox.getChildren().add(qTextField);

        nTextField.setPromptText("n = p*q: n > 256");
        nTextField.setEditable(false);
        nTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(nTextField);

        mTextField.setPromptText("m: (p-1)*(q-1)");
        mTextField.setEditable(false);
        mTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(mTextField);

        eTextField.setTooltip(new Tooltip("Prime, not part of prime factorization of m, less than m"));
        eTextField.setPromptText("e: Prime Integer, read tooltip");
        eTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(eTextField);

        dTextField.setPromptText("d: extended euclidean algorithm");
        dTextField.setEditable(false);
        dTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        root.getChildren().add(dTextField);

        TextField partnerNTextField = new TextField();
        partnerNTextField.setPromptText("");
        root.getChildren().add(partnerNTextField);

        root.getChildren().add(okCancel);

        Scene popupScene = new Scene(root);
        popupScene.setFill(Color.rgb(127, 127, 127, 0.5));

        return popupScene;
    }

    /*
    Return true if the values of all the given TextFields are valid, changes values of TextFields according to other
    TextFields and changes the field's (p, q, e) values accordingly. Is supporsed to be called if any TextField's value
    changes.
     */
    private boolean checkRSAEntryValidity(TextField pTf, TextField qTf, TextField eTf, TextField nTf, TextField mTf, TextField dTf) {
        int p = -1, q = -1, e = -1;
        boolean pg = false, qg = false, eg = false;
        if (isInteger(pTf.getText())) {
            p = Integer.parseInt(pTf.getText());
            if (Cipher.isPrimeNumber(p)) pg = true;
        }

        if (isInteger(qTf.getText())) {
            q = Integer.parseInt(qTf.getText());
            if (Cipher.isPrimeNumber(q)) qg = true;
        }

        if (p * q <= 256) {
            pg = false;
            qg = false;
        }

        if(isInteger(eTf.getText())) {
            e = Integer.parseInt(eTf.getText());
            if(p > 0 && q > 0) {
                int m = (p - 1) * (q - 1);
                if(e < m && !Cipher.primeFactorizationOfMContainsPrime(m, e) && e > 1) eg = true;
            }
        }

        AppView.goodOrBadTextField(pTf, pg);
        AppView.goodOrBadTextField(qTf, qg);
        AppView.goodOrBadTextField(eTf, eg);

        if(pg && qg) {
            nTf.setText(Integer.toString(p * q));
            mTf.setText(Integer.toString((p - 1) * (q - 1)));
            if(eg) dTf.setText("8869 xd");
        } else {
            nTf.setText("");
            mTf.setText("");
            dTf.setText("");
        }

        if(pg && qg && eg){
            getCipher().getRsa().generateKeys(p, q, e);
        }


        return !pg || !qg || !eg;
    }

    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public int getCaesarKey() {
        return caesarKey;
    }

    private void setCaesarKey(int caesarKey) {
        this.caesarKey = caesarKey;
    }

    public int getpRSA() {
        return pRSA;
    }

    private void setpRSA(int pRSA) {
        this.pRSA = pRSA;
    }

    public int getqRSA() {
        return qRSA;
    }

    private void setqRSA(int qRSA) {
        this.qRSA = qRSA;
    }

    public int geteRSA() {
        return eRSA;
    }

    private void seteRSA(int eRSA) {
        this.eRSA = eRSA;
    }

    public String getVigenereKey() {
        return vigenereKey;
    }

    private void setVigenereKey(String vigenereKey) {
        this.vigenereKey = vigenereKey;
    }

    private Scene getMono() {
        return mono;
    }

    private Scene getPoly() {
        return poly;
    }

    private Scene getRsa() {
        return rsa;
    }

    private Stage getPopup() {
        return popup;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}
