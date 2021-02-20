package client.data.cipher;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Rsa {

    //Private key: n, e
    //Public key: n, d

    private int n, e, d;

    public Rsa() {
    }

    private String decrypt(int[] encryptedChars) {
        //Decrypting
        byte[] chars = new byte[encryptedChars.length];
        for(int i = 0; i < chars.length; i++) {
            chars[i] = (byte) modPow(encryptedChars[i], d, n);
        }

        for(Byte i : chars) {
            System.out.println(i);
        }

        return new String(chars, StandardCharsets.UTF_8);
    }

    private int[] encrypt(String message) {
        //Decoding
        byte[] chars = message.getBytes(StandardCharsets.UTF_8);
        for(Byte i : chars) {
            System.out.println(i);
        }

        //Encrypting
        int[] encryptedChars = new int[chars.length];
        for(int i = 0; i < chars.length; i ++) {
            encryptedChars[i] = modPow(chars[i], e, n);
        }
        return encryptedChars;
    }

    public void generateKeys(int p, int q, int e) {

        //n > alphabet.length (ascii = 128, UTF-8 = 256)
        int n = p * q;
        int m = (p - 1) * (q - 1);
        int mo = m;

        //Primzahl, kein Teil der Primzahlzerlegung von m, kleiner als m
        int eo = e;
        //Teilerfremd zu m, d > 0, d * e % m = 1
        int d;

        //Berechnung von d durch erweiterten euklidischen Algorithmus
        int edm, emm = m, a = 0, b = 1, bo;
        ArrayList<Integer> edml = new ArrayList<Integer>();
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

        this.n = n;
        this.e = eo;
        this.d = d;
    }

    public int modPow(int base, int exponent, int mod) {
        return new BigInteger(Integer.toString(base)).modPow(
                new BigInteger(Integer.toString(exponent)),
                new BigInteger(Integer.toString(mod))
        ).intValue();
    }
}
