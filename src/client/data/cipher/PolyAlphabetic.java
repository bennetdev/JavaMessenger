package client.data.cipher;

public class PolyAlphabetic {

    private String key;

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
        this.key = key;
    }
}