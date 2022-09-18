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

import mpi.*;

public class Main {

    public static Scanner scanner = new Scanner(System.in);
    public static int rank;
    public static int size;

    public static void main(String[] args) throws MPIException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {
        try {
            MPI.Init(args);
            //mpi logging
            rank = MPI.COMM_WORLD.getRank();
            size = MPI.COMM_WORLD.getSize();
            System.out.println("INFO:  = " + rank + ", size = " + size);

            byte[] privateKey = requestPrivateKey();
            File cipherFile = new File(Properties.getFileLocation() + Properties.getCipherName());
            initCipherFile(cipherFile, privateKey);

            StringBuilder plainTextContent = decrypt(cipherFile, privateKey);

            while(true) {

                String choice = "";
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
                    if(!isValidChoice(choice, "main"))
                        continue;

                    for(int proc = 1 ; proc < size ; proc++) { //send the input to the other node
                        MPI.COMM_WORLD.send(choice.toCharArray(), choice.length(), MPI.CHAR, proc, 98);
                    }
                    MPI.COMM_WORLD.barrier();

                } else {
                    mpi.Status status = null;
                    status = MPI.COMM_WORLD.probe(0, 98);
                    int inputLength = status.getCount(MPI.CHAR);
                    char[] message = new char [inputLength];
                    MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 98);
                    choice = new String(message);

                    MPI.COMM_WORLD.barrier();
                }

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

                        String answer = "";
                        if(rank == 0) {

                            System.out.print("\n INFO: Cipher file not saved yet. Save changes by writing 'save'\n ");
                            answer = scanner.nextLine();

                            for(int proc = 1 ; proc < size ; proc++) { //send the input to the other node
                                MPI.COMM_WORLD.send(answer.toCharArray(), answer.length(), MPI.CHAR, proc, 90);
                            }
                            MPI.COMM_WORLD.barrier();
                        } else {

                            mpi.Status status = null;
                            status = MPI.COMM_WORLD.probe(0, 90);
                            int inputLength = status.getCount(MPI.CHAR);
                            char[] message = new char [inputLength];
                            MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 90);
                            answer = new String(message);

                            MPI.COMM_WORLD.barrier();
                        }

                        if(answer.equalsIgnoreCase("save")) {
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

                        if(rank == 0) {

                            System.out.print("\n INFO: Cipher file not saved yet. Save changes by writing 'save'\n ");
                            answer = scanner.nextLine();

                            for(int proc = 1 ; proc < size ; proc++) { //send the input to the other node
                                MPI.COMM_WORLD.send(answer.toCharArray(), answer.length(), MPI.CHAR, proc, 88);
                            }
                            MPI.COMM_WORLD.barrier();
                        } else {

                            mpi.Status status = null;
                            status = MPI.COMM_WORLD.probe(0, 88);
                            int inputLength = status.getCount(MPI.CHAR);
                            char[] message = new char [inputLength];
                            MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 88);
                            answer = new String(message);

                            MPI.COMM_WORLD.barrier();
                        }

                        if(answer.equalsIgnoreCase("save")) {
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
