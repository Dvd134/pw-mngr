package com.dde.crypto;

import java.io.Console;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.dde.Main.scanner;
import static com.dde.Main.rank;
import static com.dde.Main.size;
import static com.dde.utils.Utils.loadProperties;

import mpi.*;

public class CryptoUtil {

    private static final int noOfBytes = 16;

    public static byte[] requestPrivateKey() throws MPIException, NoSuchAlgorithmException {

        Console console = System.console();
        byte[] privateKey;
        while(true) {

            String input = "";
            if(rank == 0) {
                input = String.valueOf(console.readPassword("\n Private key: "));
                for(int proc = 1 ; proc < size ; proc++) { //send the privateKey to the other nodes
                    MPI.COMM_WORLD.send(input.toCharArray(), input.length(), MPI.CHAR, proc, 99);
                }
                System.out.print("\n");

                MPI.COMM_WORLD.barrier();
            } else {
                mpi.Status status = null;
                status = MPI.COMM_WORLD.probe(0, 99);
                int inputLength = status.getCount(MPI.CHAR);
                char[] message = new char[inputLength];
                MPI.COMM_WORLD.recv(message, inputLength, MPI.CHAR, 0, 99);
                input = new String(message);

                MPI.COMM_WORLD.barrier();
            }

            privateKey = CryptoUtil.getRandomBytes(input.getBytes(StandardCharsets.UTF_8));

            try {
                loadProperties(privateKey);
                break;
            } catch(NegativeArraySizeException nase) {
                if(rank == 0) {

                    System.out.print("\n INFO: Private key is: " + getHex(privateKey));
                    System.out.print("\n INFO: Get the private key, init properties file and rerun the app! \n INFO: Generating 4 random IV...");
                    for(int i = 0 ; i < 4 ; i++) {
                        System.out.print("\n " + getHex(getRandomBytes(null)));
                    }
                }
                System.exit(0);
            } catch (FileNotFoundException fne) {
                System.out.print("\n ERROR(" + rank + "): cipher.properties not found!");
                fne.printStackTrace();
                System.exit(0);
            } catch(Exception e) {
                System.out.print("\n ERROR(" + rank + "): Invalid private key");
                e.printStackTrace();
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
