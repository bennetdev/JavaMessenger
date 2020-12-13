package client.data;

public class PolyAlphabetic {
    // Encrypt with Vigenere. Expects Decrypted String and key as String
    public String vigenereEncryption(String text, String key) {
        // Strings to char arrays
        char[] chars = text.toCharArray();
        char[] keyChars = key.toCharArray();
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            // Repeat keyword if necessary
            char keyChar = keyChars[i % key.length()];
            result += Cipher.moveChar(c, Cipher.getAlphabet().indexOf(keyChar));
        }
        return result;
    }

    // Decrypt with Vigenere. Expects Decrypted String and key as String
    public String vigenereDecryption(String text, String key) {
        // Strings to char arrays
        char[] chars = text.toCharArray();
        char[] keyChars = key.toCharArray();
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            // Repeat keyword if necessary
            char keyChar = keyChars[i % key.length()];
            result += Cipher.moveChar(c, -Cipher.getAlphabet().indexOf(keyChar));
        }
        return result;
    }
}
