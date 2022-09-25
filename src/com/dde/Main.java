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

import static com.dde.crypto.CryptoUtil.getHex;
import static com.dde.crypto.CryptoUtil.requestPrivateKey;
import static com.dde.crypto.Decryption.decrypt;
import static com.dde.utils.BusinessLogic.isValidChoice;
import static com.dde.utils.MenuBusinessLogic.*;
import static com.dde.utils.Utils.*;

import mpi.*;

public class Main {

    // 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
    public static Scanner scanner = new Scanner(System.in);
    public static int rank;
    public static int size;

    public static void main(String[] args) throws MPIException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {

        try {
            MPI.Init(args);
            //mpi logging
            rank = MPI.COMM_WORLD.getRank();
            size = MPI.COMM_WORLD.getSize();
            System.out.println("INFO:  Process " + rank + "/" + size);

            byte[] privateKey;

            if (rank == 0) {
                privateKey = requestPrivateKey();
                for (int proc = 1; proc < size; proc++) { //send the privateKey to the other nodes
                    MPI.COMM_WORLD.send(privateKey, privateKey.length, MPI.BYTE, proc, 1);
                }
                MPI.COMM_WORLD.barrier();
            } else {
                mpi.Status status = null;
                status = MPI.COMM_WORLD.probe(0, 1);
                int privateKeyLength = status.getCount(MPI.BYTE);
                privateKey = new byte[privateKeyLength];
                MPI.COMM_WORLD.recv(privateKey, privateKeyLength, MPI.BYTE, 0, 1);
                MPI.COMM_WORLD.barrier();
                
                loadProperties(privateKey);
            }

            File cipherFile = new File(Properties.getFileLocation() + Properties.getCipherName());
            initCipherFile(cipherFile, privateKey);

            StringBuilder plainTextContent = decrypt(cipherFile, privateKey);

            while (true) {

                String choice;
                if(rank == 0) {
                    System.out.println("""
                             
                             Menu:
                             1. Filter
                             2. Add password
                             3. Update password
                             4. Show all
                             5. Delete password
                             0. Exit
                            """);
                    choice = scanner.nextLine();
                    if (!isValidChoice(choice, "main"))
                        continue;

                    for(int proc = 1 ; proc < size ; proc++) { //send the input to other nodes
                        MPI.COMM_WORLD.send(choice.toCharArray(), choice.length(), MPI.CHAR, proc, 2);
                    }
                    MPI.COMM_WORLD.barrier();
                } else {

                    mpi.Status status = null;
                    status = MPI.COMM_WORLD.probe(0, 2);
                    int inputLength = status.getCount(MPI.CHAR);
                    char[] message = new char [inputLength];
                    MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 2);
                    choice = new String(message);

                    MPI.COMM_WORLD.barrier();
                }

                switch (choice.toLowerCase()) {
                    case "1", "filter":
                        if(rank == 0)
                            doFilter(plainTextContent);
                        break;
                    case "2", "add":
                        plainTextContent = addPassword(plainTextContent);
                        saveFile(plainTextContent, privateKey);
                        break;
                    case "3", "update":
                        plainTextContent = updatePassword(plainTextContent);
                        System.out.print("\n INFO(" + rank + "): Cipher file not saved yet. Save changes by writing 'save'\n ");

                        String answer;
                        if(rank == 0) {

                            answer = scanner.nextLine();

                            for(int proc = 1 ; proc < size ; proc++) { //send the input to the other node
                                MPI.COMM_WORLD.send(answer.toCharArray(), answer.length(), MPI.CHAR, proc, 9);
                            }
                            MPI.COMM_WORLD.barrier();
                        } else {

                            mpi.Status status = null;
                            status = MPI.COMM_WORLD.probe(0, 9);
                            int inputLength = status.getCount(MPI.CHAR);
                            char[] message = new char [inputLength];
                            MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 9);
                            answer = new String(message);

                            MPI.COMM_WORLD.barrier();
                        }

                        if (answer.equalsIgnoreCase("save")) {
                            saveFile(plainTextContent, privateKey);
                        } else {
                            System.out.print("\n WARN(" + rank + "): Changes not yet saved! Unsaved changes will automatically sync after program terminates.");
                        }
                        break;
                    case "4", "show":
                        if(rank == 0)
                            showAll(plainTextContent);
                        break;
                    case "5", "delete":
                        plainTextContent = deleteKey(plainTextContent);
                        System.out.print("\n INFO(" + rank + "): Cipher file not saved yet. Save changes by writing 'save'\n ");

                        if(rank == 0) {

                            answer = scanner.nextLine();

                            for(int proc = 1 ; proc < size ; proc++) { //send the input to the other node
                                MPI.COMM_WORLD.send(answer.toCharArray(), answer.length(), MPI.CHAR, proc, 11);
                            }
                            MPI.COMM_WORLD.barrier();
                        } else {

                            mpi.Status status = null;
                            status = MPI.COMM_WORLD.probe(0, 11);
                            int inputLength = status.getCount(MPI.CHAR);
                            char[] message = new char [inputLength];
                            MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 11);
                            answer = new String(message);

                            MPI.COMM_WORLD.barrier();
                        }

                        if (answer.equalsIgnoreCase("save")) {
                            saveFile(plainTextContent, privateKey);
                        } else {
                            System.out.print("\n WARN(" + rank + "): Changes not yet saved! Unsaved changes will automatically sync after program terminates.");
                        }
                        break;
                    case "0", "exit":
                        terminateProgram(plainTextContent, privateKey);
                }
            }
        } catch(MPIException mpie) {
            mpie.printStackTrace();
        } finally {
            MPI.Finalize();
        }
    }
}
