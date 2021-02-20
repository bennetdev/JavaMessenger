package client.data.cipher;

public class Cipher {
    private static final int UTF_MAX_VALUE = 65535;

    public static void main(String[] args) {
        MonoAlphabetic.test(42069);
        PolyAlphabetic.test("xd deine mudda stinkt nach fisch!");
    }

    public Cipher(){

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
}
