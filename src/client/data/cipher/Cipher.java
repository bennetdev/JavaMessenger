package client.data.cipher;

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
        String alphabet = Character.isLowerCase(c) ? ALPHABET_LOWERCASE : ALPHABET;
        System.out.println(c + " " + alphabet);
        return alphabet.toCharArray()[Math.abs(alphabet.indexOf(c) + key) % 26];
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

    public static String getAlphabetLowercase() {
        return ALPHABET_LOWERCASE;
    }
}
