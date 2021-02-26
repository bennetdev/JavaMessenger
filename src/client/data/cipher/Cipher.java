package client.data.cipher;

import client.data.Message;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

/*
Every chat has an instance of Cipher which holds the outgoing encryption method and the keys. It is stored locally on
a per-chat basis to provide remembering the keys and encryption method. Instances of cipher also provide a way for chats
to access the encryption/decryption functions of the respective encryption methods. Cipher also holds all the utility
functions and constants needed within the cipher package.
 */
public class Cipher implements Serializable {
    private static final long serialVersionUID = 1890527805564045271L;

    private static final transient int UTF_MAX_VALUE = 65535;
    private static final transient ArrayList<Long> PRIMES = new ArrayList<>();

    private MonoAlphabetic monoAlphabetic;
    private PolyAlphabetic polyAlphabetic;
    private Rsa rsa;
    private Message.EncryptionMethod encryptionMethod;


    public Cipher(){
        setMonoAlphabetic(new MonoAlphabetic());
        setPolyAlphabetic(new PolyAlphabetic());
        setRsa(new Rsa());
    }

    public static Long getNthPrime(int index) {
        if(getPRIMES().size() < 1) getPRIMES().add(2L);
        // i = maxIndex, n = goal index. When n = i, for-loop will stop
        if(index < 0) return -1L;
        for(int i = getPRIMES().size() - 1; i < index; i++) {
            getPRIMES().add(nextPrime(getPRIMES().get(i)));
        }

        return getPRIMES().get(index);
    }

    public static int indexOfPrime(long prime) {
        if(!isPrime(prime)) return -1;
        int i = getPRIMES().indexOf(prime);
        if(i >= 0) return i;
        else for(i = getPRIMES().size() - 1; getPRIMES().get(i) != prime; i++) {
            getPRIMES().add(nextPrime(getPRIMES().get(i)));
        }
        return i;
    }

    public static boolean isEPrimeFactorOfM(long m, long e) {
        return primeFactorizationInternal(m).contains(e);
    }

    public static String primeFactorization(long m) {
        return primeFactorizationInternal(m).toString();
    }

    private static ArrayList<Long> primeFactorizationInternal(long m) {
        ArrayList<Long> factorization = new ArrayList<>();
        for(long i = 2; i <= m; i++) {
            while(m % i == 0) {
                if(!factorization.contains(i)) factorization.add(i);
                m = m/i;
            }
        }
        return factorization;
    }


    public static long nextPrime(long num) {
        return new BigInteger(String.valueOf(num)).nextProbablePrime().longValueExact();
    }

    //certainty = 99,9999%
    public static boolean isPrime(long num){
        return new BigInteger(String.valueOf(num)).isProbablePrime(20);
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

    public static ArrayList<Long> getPRIMES() {
        return PRIMES;
    }

    public Message.EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(Message.EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
