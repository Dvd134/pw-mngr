package com.dde.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Decryption {

    public static StringBuilder decrypt(File cipherFile, byte[] privateKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        FileInputStream fis = new FileInputStream(cipherFile);
        BufferedInputStream bis = new BufferedInputStream(fis);

        byte[] IV = new byte[CryptoUtil.getNoOfBytes()];
        int nBytes = bis.read(IV);

        if(nBytes < CryptoUtil.getNoOfBytes()) {
            System.out.println("\n ERROR: Error reading IV");
            System.exit(0);
        }

        Cipher cipher = Cipher.getInstance(Encryption.getEncryptionAlgorithm() + Encryption.getEncryptionBlockCipherMode());
        SecretKeySpec keySpec = new SecretKeySpec(privateKey, Encryption.getEncryptionAlgorithm());
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        StringBuilder content = new StringBuilder();
        byte[] buffer = new byte[CryptoUtil.getNoOfBytes()];
        byte[] outputBuffer = null;
        int noInputBytes = 0;

        while((noInputBytes = bis.read(buffer)) != -1) {
            outputBuffer = cipher.update(buffer,0, noInputBytes);
            content.append(new String(outputBuffer, StandardCharsets.UTF_8));
        }
        outputBuffer = cipher.doFinal();
        content.append(new String(outputBuffer, StandardCharsets.UTF_8));

        fis.close();
        return content;
    }

    public static String decryptProperty(String base64Cipher, byte[] privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] cipherText = Base64.getDecoder().decode(base64Cipher);
        byte[] IV = new byte[CryptoUtil.getNoOfBytes()];
        byte[] content = new byte[cipherText.length - IV.length];

        System.arraycopy(cipherText, 0, IV, 0, IV.length);
        System.arraycopy(cipherText, IV.length, content, 0, content.length);

        Cipher cipher = Cipher.getInstance(Encryption.getEncryptionAlgorithm() + Encryption.getEncryptionBlockCipherMode());
        SecretKeySpec keySpec = new SecretKeySpec(privateKey, Encryption.getEncryptionAlgorithm());
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] plainText = cipher.doFinal(content);
        return new String(plainText);
    }
}
