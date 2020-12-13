package client.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    public enum EncryptionMethod{
        CAESAR, VIGENERE, RSA
    }

    private String from;
    private String to;
    private String text;
    private LocalDateTime timeSend;
    private EncryptionMethod encryptionMethod;

    public Message(String from, String to, String text, EncryptionMethod encryptionMethod) {
        this.from = from;
        this.to = to;
        this.text = text;
        setEncryptionMethod(encryptionMethod);
        setTimeSend(LocalDateTime.now());
    }

    public void encrypt(String key, Cipher cipher){
        switch (getEncryptionMethod()){
            case CAESAR:
                // Encrypt with caesar
                setText(cipher.getMonoAlphabetic().caeserEncryption(getText(), Integer.parseInt(key)));
                break;
            case VIGENERE:
                // Encrypt with Vigenere
                setText(cipher.getPolyAlphabetic().vigenereEncryption(getText(), key));
                break;
            case RSA:
                // Encrypt with RSA
                break;
        }
    }
    public void decrypt(String key, Cipher cipher){
        switch (getEncryptionMethod()){
            case CAESAR:
                // Decrypt with caesar
                setText(cipher.getMonoAlphabetic().caeserDecryption(getText(), Integer.parseInt(key)));
                break;
            case VIGENERE:
                // Decrypt with Vigenere
                setText(cipher.getPolyAlphabetic().vigenereDecryption(getText(), key));
                break;
            case RSA:
                // Decrypt with RSA
                break;

        }
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

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

    public LocalDateTime getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(LocalDateTime timeSend) {
        this.timeSend = timeSend;
    }
}
