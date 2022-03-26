package com.dde;

import com.dde.entities.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static com.dde.crypto.CryptoUtil.requestPrivateKey;
import static com.dde.crypto.Decryption.decrypt;
import static com.dde.utils.BusinessLogic.isValidChoice;
import static com.dde.utils.MenuBusinessLogic.*;
import static com.dde.utils.Utils.*;

public class Main {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {

        byte[] privateKey = requestPrivateKey();
        File cipherFile = new File(Properties.getFileLocation() + Properties.getCipherName());
        initCipherFile(cipherFile, privateKey);

        StringBuilder plainTextContent = decrypt(cipherFile, privateKey);

        while(true) {

            System.out.println("""
                     Menu:
                     1. Filter
                     2. Add password
                     3. Update password
                     4. Show all
                     5. Delete password
                     0. Exit
                    """);
            String choice = scanner.nextLine();
            if(!isValidChoice(choice, "main"))
                continue;

            switch(choice.toLowerCase()) {
                case "1", "filter":
                    doFilter(plainTextContent);
                    break;
                case "2", "add":
                    plainTextContent = addPassword(plainTextContent);
                    saveFile(plainTextContent, privateKey);
                    break;
                case "3", "update":
                    plainTextContent = updatePassword(plainTextContent);
                    System.out.print("\n INFO: Cipher file not saved yet. Save changes by writing 'save'\n ");
                    String answer = scanner.nextLine();
                    if(answer.equalsIgnoreCase("save")) {
                        saveFile(plainTextContent, privateKey);
                    } else {
                        System.out.print("\n WARN: Changes not yet saved! Unsaved changes will automatically sync after program terminates.");
                    }
                    break;
                case "4", "show":
                    showAll(plainTextContent);
                    break;
                case "5", "delete":
                    plainTextContent = deleteKey(plainTextContent);
                    System.out.print("\n INFO: Cipher file not saved yet. Save changes by writing 'save'\n ");
                    answer = scanner.nextLine();
                    if(answer.equalsIgnoreCase("save")) {
                        saveFile(plainTextContent, privateKey);
                    } else {
                        System.out.print("\n WARN: Changes not yet saved! Unsaved changes will automatically sync after program terminates.");
                    }
                    break;
                case "0", "exit":
                    terminateProgram(plainTextContent, privateKey);
            }
        }
    }
}
