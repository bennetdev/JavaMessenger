package client.data;

import java.util.Locale;

public class Cipher {
    private static final String ALPHABET = "ABCDEFGHIJKLMONPQRSTUVWXYZ";
    private static final String ALPHABET_LOWERCASE = ALPHABET.toLowerCase();

    private MonoAlphabetic monoAlphabetic;
    private PolyAlphabetic polyAlphabetic;

    public Cipher(){
        setMonoAlphabetic(new MonoAlphabetic());
        setPolyAlphabetic(new PolyAlphabetic());
    }

    // Move character in alphabet by key
    public static Character moveChar(char c, int key) {
        return Cipher.getAlphabet().toCharArray()[Math.abs(Cipher.getAlphabet().indexOf(c) + key) % 26];
    }

    public static String getAlphabet() {
        return ALPHABET;
    }

    public MonoAlphabetic getMonoAlphabetic() {
        return monoAlphabetic;
    }

    public void setMonoAlphabetic(MonoAlphabetic monoAlphabetic) {
        this.monoAlphabetic = monoAlphabetic;
    }

    public PolyAlphabetic getPolyAlphabetic() {
        return polyAlphabetic;
    }

    public void setPolyAlphabetic(PolyAlphabetic polyAlphabetic) {
        this.polyAlphabetic = polyAlphabetic;
    }
}
