package client.data.cipher;

import java.io.Serializable;

/*
Instances of MonoAlphabetic held by instances of Cipher store a key value and a not-yet-used ready field. Also they
contain encryption and decryption methods.
 */
public class MonoAlphabetic implements Serializable {
    private static final long serialVersionUID = 2855396319789958539L;

    private int key;
    private boolean ready = false;

    public MonoAlphabetic() {
        this.key = 0;
    }

    // Encrypt with Caesar. Expects decrypted String and integer as key
    public String caesarEncryption(String text) {
        // get real key length
        String result = "";
        // Encrypt every Char in text and add to result
        for (char c : text.toCharArray()) {
            result += Cipher.moveChar(c, key);
        }

        return result;
    }



    // Decrypt with Caesar. Expects encrypted String and integer as key
     public String caesarDecryption(String text) {
        String result = "";
        // Encrypt every Char in text and add to result
        for (Character c : text.toCharArray()) {
            result += Cipher.moveChar(c, -key);
        }
        return result;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key > 0 ? (key % Cipher.getUtfMaxValue()) : 0;
        setReady(getKey() > 0);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}