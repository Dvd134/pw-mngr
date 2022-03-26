package com.dde.crypto;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.dde.Main.scanner;
import static com.dde.utils.Utils.loadProperties;

public class CryptoUtil {

    private static final int noOfBytes = 16;

    public static byte[] requestPrivateKey() throws NoSuchAlgorithmException {
        byte[] privateKey;
        while(true) {
            System.out.print("\n Private key: ");
            String input = scanner.nextLine();

            privateKey = CryptoUtil.getRandomBytes(input.getBytes(StandardCharsets.UTF_8));

            try {
                loadProperties(privateKey);
                break;
            } catch(Exception e) {
                System.out.print("\n ERROR: Invalid private key");
            }
        }
        return privateKey;
    }

    public static byte[] getRandomBytes(byte[] seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        if (seed != null) {
            secureRandom.setSeed(seed);
        }
        byte[] randomBytes = new byte[noOfBytes];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public static String getHex(byte[] array) {
        String output = "";
        for(byte value : array) {
            output += String.format("%02x", value);
        }
        return output;
    }

    public static int getNoOfBytes() {
        return noOfBytes;
    }
}