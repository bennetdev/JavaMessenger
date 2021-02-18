package client.data.cipher;

public class Cipher {
    private static final String ALPHABET = "ABCDEFGHIJKLMONPQRSTUVWXYZ";
    private static final String ALPHABET_LOWERCASE = ALPHABET.toLowerCase();

    private MonoAlphabetic monoAlphabetic;
    private PolyAlphabetic polyAlphabetic;
    private Rsa rsa;

    public Cipher(){
        setMonoAlphabetic(new MonoAlphabetic());
        setPolyAlphabetic(new PolyAlphabetic());
        setRsa(new Rsa());
    }

    //TODO: Move this function to RSA class
    public static boolean primeFactorizationOfMContainsPrime(int m, int prime) {
        boolean contains = false;
        int iSafe = -42069;
        for(int i = 2; i < m; i++) {
            iSafe = m;
            if(m % i == 0) {
                m /= i;
                if(!contains) contains = prime == i;
                i = 1;
            }
        }
        if(!contains) contains = prime == iSafe;

        return contains;
    }

    public static boolean isPrimeNumber(int e) {
        if(e < 3) return false;
        boolean flag = false;
        for (int i = 2; i <= e / 2; ++i) {
            if (e % i == 0) {
                flag = true;
                break;
            }
        }
        return !flag;
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

    public Rsa getRsa() {
        return rsa;
    }

    public void setRsa(Rsa rsa) {
        this.rsa = rsa;
    }
}
