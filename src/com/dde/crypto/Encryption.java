package com.dde.crypto;

import com.dde.entities.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.dde.crypto.CryptoUtil.getRandomBytes;

public class Encryption {

    private static String algorithm = "AES";
    private static String blockCipherMode = "/CBC/PKCS5Padding";

    public static void encrypt(StringBuilder plainTextContent, byte[] privateKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        File outputFile = new File(Properties.getFileLocation() + Properties.getCipherName());
        FileOutputStream fos = new FileOutputStream(outputFile);

        byte[] IV = getRandomBytes(null);
        fos.write(IV);

        Cipher cipher = Cipher.getInstance(algorithm + blockCipherMode);
        SecretKeySpec keySpec = new SecretKeySpec(privateKey, algorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] buffer = cipher.doFinal(plainTextContent.toString().getBytes(StandardCharsets.UTF_8));
        fos.write(buffer);

        fos.close();
    }

    public static String getEncryptionAlgorithm() {
        return algorithm;
    }

    public static String getEncryptionBlockCipherMode() {
        return blockCipherMode;
    }
}
