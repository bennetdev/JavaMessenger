package client.data.cipher;

import java.io.Serializable;

/*
Instances of PolyAlphabetic held by instances of Cipher store a key value and a not-yet-used ready field. Also they
contain encryption and decryption methods.
 */
public class PolyAlphabetic implements Serializable {
    private static final long serialVersionUID = -3752581341523828569L;

    private String key;
    private boolean ready = false;

    public PolyAlphabetic() {
        this.key = "";
    }

    // Encrypt with Vigenere. Expects Decrypted String and key as String
    public String vigenereEncryption(String text) {
        // Strings to char arrays
        char[] chars = text.toCharArray();

        //replace spaces with
        char[] keyChars = key.replaceAll(" ", "").toCharArray();
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            // Repeat keyword if necessary
            char keyChar = keyChars[i % key.length()];
            result += Cipher.moveChar(c, keyChar);
        }
        return result;
    }

    // Decrypt with Vigenere. Expects Decrypted String and key as String
    public String vigenereDecryption(String text) {
        // Strings to char arrays
        char[] chars = text.toCharArray();
        char[] keyChars = key.toCharArray();
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            // Repeat keyword if necessary
            char keyChar = keyChars[i % key.length()];
            result += Cipher.moveChar(c, -keyChar);
        }
        return result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        setReady(key.length() > 0);
        this.key = key;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}