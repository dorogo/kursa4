/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.RSA;

import java.math.BigInteger;

/**
 *
 * @author user
 */
public class RSA {

    private static volatile RSA instance;

    private int e, n;
    private BigInteger db;

    public static RSA getInstance() {
        RSA localInstance = instance;
        if (localInstance == null) {
            synchronized (RSA.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RSA();
                }
            }
        }
        return localInstance;
    }

    public void genKeys() {
        int p, q, k;
        do {
            p = (int) (Math.random() * 2000) + 2000;
            q = (int) (Math.random() * 2000) + 2000;
        } while ((p == q) || !isPrime(p) || !isPrime(q));
        n = p * q;
        k = (p - 1) * (q - 1);
        do {
            e = (int) (Math.random() * (k - 1)) + 1;
        } while (gcd(e, k) != 1);
        db = BigInteger.valueOf(e).modInverse(BigInteger.valueOf(k));
        System.out.println("Generate keys completed.");
        System.out.println("Public: {" + e + ", " + n + "}");
        System.out.println("Secret: {" + db.toString() + ", " + n + "}");
    }

    private boolean isPrime(int num) {
        int temp;
        boolean _isPrime = true;

        for (int i = 2; i <= num / 2; i++) {
            temp = num % i;
            if (temp == 0) {
                _isPrime = false;
                break;
            }
        }
        return _isPrime;
    }

    //НОД
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        int x = a % b;
        return gcd(b, x);
    }

    public int[] getPublicKeys() {
        return new int[]{e, n};
    }

    public int[] getSecretKeys() {
        return new int[]{db.intValue(), n};
    }

    public static String encryptRSA(String srcText, int p, int n) {
        System.out.println(p + " " + n + " Source text:" + srcText);
        String encryptedText = "";
        int l = srcText.length();
        BigInteger c, m;
        for (int i = 0; i < l; i++) {
            m = BigInteger.valueOf((long) srcText.charAt(i));
            c = m.modPow(BigInteger.valueOf((long) p), BigInteger.valueOf((long) n));
            encryptedText += "," + c.toString();
        }
        return encryptedText;
    }

    public static String decryptRSA(String srcText, int secret, int n) {
        String decryptedText = "";
        String tmp = "";
        int l = srcText.length();
        BigInteger c, m;
        for (int i = 1; i < l; i++) {
            if (Character.toString(srcText.charAt(i - 1)).matches("[,]")) {
                tmp = "";
                do {
                    tmp += srcText.charAt(i);
                    i++;
                } while (i < l && srcText.charAt(i) != ',');
                c = BigInteger.valueOf((long) Integer.parseInt(tmp));
                m = c.modPow(BigInteger.valueOf((long) secret), BigInteger.valueOf((long) n));
                decryptedText += (char) m.intValue();
            }
        }
        return decryptedText;
    }

}
