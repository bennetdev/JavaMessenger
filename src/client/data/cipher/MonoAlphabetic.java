package client.data.cipher;

public class MonoAlphabetic {
    public static void test(int key) {
        System.out.println(caesarEncryption("abc Moin88 xD", key));
        System.out.println(caesarDecryption(caesarEncryption("abc Moin88 xD", key), key));
        System.out.println();
    }
    
    // Encrypt with Caesar. Expects decrypted String and integer as key
    public static String caesarEncryption(String text, int key) {
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
     public static String caesarDecryption(String text, int key) {
        // get real key length

        key = key > 0 ? (key % Cipher.getUtfMaxValue()) : 0;
        String result = "";
        // Encrypt every Char in text and add to result
        for (Character c : text.toCharArray()) {
            result += Cipher.moveChar(c, -key);
        }
        return result;
    }
}