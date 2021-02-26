package client.data.cipher;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/*
Instances of Rsa held by instances of Cipher store all its key values (public, private, public of partner) and a
not-yet-used ready field. Also they contain encryption and decryption methods. In addition, they are able to fill
themselves with random valid keys on request.
 */
public class Rsa implements Serializable {
    private static final long serialVersionUID = -2562309474521057829L;
    private static final transient Random RANDOM = new Random();

    // Private key: n, d
    // Public key:  n, e
    // Initialize n, e, d to 1 so that an arithmetic exception is avoided on sending without proper rsa settings.
    private long n, e, d, partnerN = 1, partnerE;

    private boolean ready = false;

    // Only for GUI
    private long p;
    private long q;

    public static void main(String[] args) {

        Rsa r1 = new Rsa();
        r1.generateKeys(r1.getRandomPQE());

        Rsa r2 = new Rsa();
        r2.generateKeys(r2.getRandomPQE());

        r1.setPartnerKey(r2.getE(), r2.getN());
        r2.setPartnerKey(r1.getE(), r1.getN());

        System.out.println(r1.decrypt(r2.encrypt("abc 123 hello world &à~")));
    }


    // Type cycle: long[] -> char[imaginaryArray] -> StringBuilder -> String
    public String decrypt(long[] cipher) {
        StringBuilder text = new StringBuilder();

        for(long ch : cipher) {
            text.append((char) (modPow(ch, getD(), getN())));
        }
        return text.toString();
    }


    // Type cycle: String -> char[imaginaryArray] -> long[]
    public long[] encrypt(String text) {
        long[] cipher = new long[text.length()];

        for(int i = 0; i < cipher.length; i++) {
            cipher[i] = modPow(text.charAt(i), getPartnerE(), getPartnerN());
        }
        return cipher;
    }


    public long[] getRandomPQE() {
        long utfMax = Cipher.getUtfMaxValue();

        // restricted to prevent unnecessary computational load
        long maxPQValue = utfMax / 50, maxEValue = utfMax / 500;

        // p: prime from (2) to (Cipher.getUtfMaxValue() + a bit bc of nextPrime())
        long p = -1;
        while(p <= maxPQValue / 10) p = Cipher.nextPrime((Math.abs(getRANDOM().nextLong())) % maxPQValue);

        // q: prime from (c/p + 1) to (Cipher.getUtfMaxValue() + a bit bc of nextPrime())
        long q = -1;
        while(q <= utfMax / p) q = Cipher.nextPrime((Math.abs(getRANDOM().nextLong())) % maxPQValue);

        long m = (p - 1) * (q - 1);

        // e: first successive prime that is less than m and not part of m's prime factorization from (3) to (maxEValue)
        long e = Cipher.nextPrime((Math.abs(getRANDOM().nextLong())) % maxEValue);
        while(e >= m || Cipher.isEPrimeFactorOfM(m, e)) {
            e = Cipher.nextPrime(e);
        }

        return new long[] {p, q, e};
    }

//    public void generateKeys() {
//        long[] pqe = getRandomPQE();
//        generateKeys(pqe[0], pqe[1], pqe[2]);
//    }

    public void generateKeys(long[] pqe) {
        generateKeys(pqe[0], pqe[1], pqe[2]);
    }

    /*
    e: Prime, not part of prime factorization of m, smaller than m
    p, q: Prime, big enough so that n is valid
     */
    public void generateKeys(long p, long q, long e) {
        setP(p);
        setQ(q);
        setE(e);

        // n: greater than alphabetLength = 65532
        long n = p * q;
        this.setN(n);

        long m = (p - 1) * (q - 1);

        // Calculating d using extended euclidean algorithm
        long d, edm, emm = m, a = 0, b = 1, bo, mo = m;
        ArrayList<Long> edml = new ArrayList<>();
        m = e;

        while(emm != 0) {
            e = m;
            m = emm;
            edm = e / m;
            edml.add(edm);
            emm = e % m;
        }

        for(int i = edml.size() - 2; i >= 0; i --) {
            bo = b;
            b = a - (edml.get(i) * b);
            a = bo;
        }

        d = a;
        if(d <= 0) d += mo;

        this.setD(d);


        setReady(true);
    }

    private long modPow(long base, long exponent, long mod) {
        return new BigInteger(Long.toString(base)).modPow(
                new BigInteger(Long.toString(exponent)),
                new BigInteger(Long.toString(mod))
        ).longValueExact();
    }

    public long getN() {
        return n;
    }

    private void setN(long n) {
        this.n = n;
    }

    public long getE() {
        return e;
    }

    private void setE(long e) {
        this.e = e;
    }

    public long getD() {
        return d;
    }

    private void setD(long d) {
        this.d = d;
    }

    public void setPartnerKey(long e, long n) {
        setPartnerE(e);
        setPartnerN(n);
    }

    public long getPartnerN() {
        return partnerN;
    }

    private void setPartnerN(long partnerN) {
        this.partnerN = partnerN;
    }

    public long getPartnerE() {
        return partnerE;
    }

    private void setPartnerE(long partnerE) {
        this.partnerE = partnerE;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public static Random getRANDOM() {
        return RANDOM;
    }

    public long getP() {
        return p;
    }

    public void setP(long p) {
        this.p = p;
    }

    public long getQ() {
        return q;
    }

    public void setQ(long q) {
        this.q = q;
    }
}
