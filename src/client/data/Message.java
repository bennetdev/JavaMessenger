package client.data;

import client.data.cipher.Cipher;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    public static final long serialVersionUID = -4787108556148621714L;
    public enum EncryptionMethod {
        NOT_ENCRYPTED("No end to end encryption"), CAESAR("Caesar"), VIGENERE("Vigenère"), RSA("RSA");

        private final String label;
        EncryptionMethod(String label) {
            this.label = label;
        }
        public String toString() {
            return label;
        }
    }

    private String from;
    private String to;
    private String text;
    private LocalDateTime timeSend;
    private EncryptionMethod encryptionMethod;

    public Message(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
        setEncryptionMethod(encryptionMethod);
        setTimeSend(LocalDateTime.now());
    }

    public void encrypt(Cipher cipher){
        switch (getEncryptionMethod()){
            case CAESAR:
                // Encrypt with caesar
                setText(cipher.getMonoAlphabetic().caesarEncryption(getText()));
                break;
            case VIGENERE:
                // Encrypt with Vigenere
                setText(cipher.getPolyAlphabetic().vigenereEncryption(getText()));
                break;
            case RSA:
                // Encrypt with RSA
                setText(cipher.getRsa().encrypt(getText()));
                break;
        }
    }
    public void decrypt(Cipher cipher){
        switch (getEncryptionMethod()){
            case CAESAR:
                // Decrypt with caesar
                setText(cipher.getMonoAlphabetic().caesarDecryption(getText()));
                break;
            case VIGENERE:
                // Decrypt with Vigenere
                setText(cipher.getPolyAlphabetic().vigenereDecryption(getText()));
                break;
            case RSA:
                // Decrypt with RSA
                setText(cipher.getRsa().decrypt(getText()));
                break;

        }
    }

    @Override
    public String toString() {
        return "Text: \"" + getText() + "\" From: " + getFrom() + " To: " + getTo();
    }

    public String toStringLastMessage() {
        if(from == null || from.isEmpty() || to == null || to.isEmpty()) return "Data corruption error";
        else if(text == null || text.isEmpty()) return "Empty message";

        //Returns without newLines
        else return text.replaceAll("\\n", "  ");
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(LocalDateTime timeSend) {
        this.timeSend = timeSend;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
