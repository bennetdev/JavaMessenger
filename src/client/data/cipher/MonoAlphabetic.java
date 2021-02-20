package client.data.cipher;

public class MonoAlphabetic {

    private int key;

    public MonoAlphabetic() {
        this.key = 0;
    }

    // Encrypt with Caesar. Expects decrypted String and integer as key
    public String caesarEncryption(String text) {
        // get real key length
        key = key > 0 ? (key % Cipher.getUtfMaxValue()) : 0;
        String result = "";
        // Encrypt every Char in text and add to result
        for (char c : text.toCharArray()) {
            result += Cipher.moveChar(c, key);
        }

        return result;
    }



    // Decrypt with Caesar. Expects encrypted String and integer as key
     public String caesarDecryption(String text) {
        // get real key length

        key = key > 0 ? (key % Cipher.getUtfMaxValue()) : 0;
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
        this.key = key;
    }
}