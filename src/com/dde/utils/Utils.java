package com.dde.utils;

import com.dde.crypto.Decryption;
import com.dde.crypto.Encryption;
import com.dde.entities.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static void loadProperties(byte[] privateKey) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        java.util.Properties p = new java.util.Properties();
        p.load(Utils.class.getResourceAsStream("/cipher.properties"));

        new Properties(Decryption.decryptProperty(p.getProperty("fileLocation"), privateKey), Decryption.decryptProperty(p.getProperty("cipherName"), privateKey), Decryption.decryptProperty(p.getProperty("mainMenuOptions"), privateKey), Decryption.decryptProperty(p.getProperty("updateKeyMenuOptions"), privateKey));
    }

    public static void initCipherFile(File cipherFile, byte[] privateKey) {
        if(!cipherFile.exists()) {
            System.out.println("\n WARN: " + Properties.getCipherName() + " could not be located at: " + Properties.getFileLocation());
            try {
                Encryption.encrypt(new StringBuilder().append("ID <---> PASSWORD <---> TAGS").append(System.lineSeparator()), privateKey);
            } catch(IOException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException e) {
                System.out.print("\n ERROR: " + Properties.getCipherName() + " could not be created at: " + Properties.getCipherName());
                e.printStackTrace();
            }
        }
    }

    public static void saveFile(StringBuilder plainTextContent, byte[] privateKey) {
        try {
            Encryption.encrypt(plainTextContent, privateKey);
            System.out.print("\n INFO: Encrypted file saved successfully!");
        } catch(Exception e) {
            System.out.print("\n ERROR: Encrypted file failed to be saved! Any changes will be discarded");
            e.printStackTrace();
        }
    }

    public static void terminateProgram(StringBuilder plainTextContent, byte[] privateKey) {
        saveFile(plainTextContent, privateKey);
        plainTextContent.delete(0, plainTextContent.length());
        System.exit(0);
    }
}
