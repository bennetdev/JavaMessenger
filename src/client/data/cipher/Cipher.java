package client.data.cipher;

public class Cipher {
    private static final int UTF_MAX_VALUE = 65535;

    MonoAlphabetic monoAlphabetic;
    PolyAlphabetic polyAlphabetic;
    Rsa rsa;

    public Cipher(){
        setMonoAlphabetic(new MonoAlphabetic());
        setPolyAlphabetic(new PolyAlphabetic());
        setRsa(new Rsa());
    }

    //TODO: Move this function to RSA class
    public static boolean primeFactorizationOfMContainsPrime(int m, int prime) {
        boolean contains = false;
        int iSafe = -1;
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
    public static char moveChar(char c, int key) {
        int num = (int) c + key;
        //                                                     - 1 maybe isn't needed - not tested
        if(num >= Cipher.getUtfMaxValue()) return (char) ((num - 1) % Cipher.getUtfMaxValue());
        else return (char) num;
    }

    public static int getUtfMaxValue() {
        return UTF_MAX_VALUE;
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

    public Rsa getRsa() {
        return rsa;
    }

    public void setRsa(Rsa rsa) {
        this.rsa = rsa;
    }
}
