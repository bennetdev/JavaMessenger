package client.data.cipher;

public class MonoAlphabetic {
    // Encrypt with caeser. Expects Decrypted String and key as integer
    public String caeserEncryption(String text, int key) {
        // get real key length
        key = key > 0 ? (key % 26) : 0;
        String result = "";
        // Encrypt every Char in text and add to result
        for (char c : text.toCharArray()) {
            if(Cipher.getAlphabet().indexOf(c) != -1 || Cipher.getAlphabetLowercase().indexOf(c) != -1){
                result += Cipher.moveChar(c, key);
            } else{
                result += c;
            }

        }
        return result;
    }



    // Decrypt with caeser. Expects Encrypted String and key as integer
    public String caeserDecryption(String text, int key) {
        // get real key length
        key = key > 0 ? (key % 26) : 0;
        String result = "";
        // Encrypt every Char in text and add to result
        for (Character c : text.toCharArray()) {
            if(Cipher.getAlphabet().indexOf(c) != -1 || Cipher.getAlphabetLowercase().indexOf(c) != -1) {
                result += Cipher.moveChar(c, -key);
            } else{
                result += c;
            }
        }
        return result;
    }
}