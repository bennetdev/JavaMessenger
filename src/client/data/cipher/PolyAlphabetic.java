package client.data.cipher;

public class PolyAlphabetic {

    public static void test(String key) {
        System.out.println(vigenereEncryption("ABC Moin88 xD", key));
        System.out.println(vigenereDecryption(vigenereEncryption("ABC Moin88 xD", key), key));
        System.out.println();
    }
    
    // Encrypt with Vigenere. Expects Decrypted String and key as String
    public static String vigenereEncryption(String text, String key) {
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
    public static String vigenereDecryption(String text, String key) {
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
}