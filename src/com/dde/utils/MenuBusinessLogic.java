package com.dde.utils;

import java.util.Properties;
import java.util.Scanner;

import static com.dde.Main.scanner;

public class MenuBusinessLogic extends BusinessLogic {

    public static void doFilter(StringBuilder plaintextContent) {

        // ---GET FILTER KEY FROM USER--- //
        System.out.print("\n Filter key: ");
        String filterKey = scanner.nextLine();
        boolean isDisplayable = false;

        // ---SEARCH IN EACH ROW--- //
        boolean skipHeader = true;
        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        for(String line : lines) {
            // ---SKIP FIRST LINE--- //
            if(skipHeader) {
                System.out.print("\n " + line);
                skipHeader = false;
                continue;
            }
            String[] keys = line.split(" <---> ")[2].split(" ");
            int id = Integer.parseInt(line.split(" <---> ")[0]);

            // ---TRY IF FILTER KEY = ID--- //
            try {
                int possibleIdValue = Integer.parseInt(filterKey);
                if(possibleIdValue == id) {
                    System.out.print("\n " + line);
                    continue;
                }
            } catch(NumberFormatException e) {
                //do nothing
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                for(int i = 0 ; i < keys.length ; i++) {
                    if(keys[i].equalsIgnoreCase(filterKey)) {
                        isDisplayable = true;
                        break;
                    }
                }
            }

            // ---VALIDATE IF PRINTABLE--- //
            if(isDisplayable) {
                isDisplayable = false;
                System.out.print("\n " + line);
            }
        }
        System.out.println("\n");
        // ---FREEZE FOR READING--- //
        scanner.nextLine();
    }

    public static StringBuilder addPassword(StringBuilder plaintextContent){

        // ---GET INPUT FROM THE USER--- //
        System.out.print("\n Keys: ");
        String keys = scanner.nextLine();

        System.out.print("\n Password: ");
        String password = scanner.nextLine();

        // ---GENERATE AN ID--- //
        String id = String.valueOf(getId(plaintextContent));

        // ---INSERT NEW LINE AT CORRESPONDING ID POSITION IN ORDERED LIST--- //
        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        StringBuilder updatedContent = new StringBuilder();
        boolean skipHeader = true;
        boolean isKeyAdded = false;
        int previousRowId = 0;
        for (String row : lines) {
            // ---SKIP FIRST LINE--- //
            if(skipHeader) {
                updatedContent.append(row).append(System.lineSeparator());
                skipHeader = false;
                // ---CHECK IF CONTENT IS EMPTY, IF TRUE ADD DIRECTLY--- //
                if(lines.length == 1) {
                    updatedContent.append(id).append(" <---> ").append(password).append(" <---> ").append(keys).append(System.lineSeparator());
                    isKeyAdded = true;
                }
                continue;
            }

            int rowId = Integer.parseInt(row.split(" <---> ")[0]);
            if (rowId - previousRowId > 1) {
                // ---BUILD THE LINE AND APPEND IT TO THE CONTENT--- //
                if(!isKeyAdded) {
                    updatedContent.append(id).append(" <---> ").append(password).append(" <---> ").append(keys).append(System.lineSeparator());
                    isKeyAdded = true;
                }

                updatedContent.append(row).append(System.lineSeparator());
            } else {
                updatedContent.append(row).append(System.lineSeparator());

            }
            previousRowId = rowId;
        }
        if(!isKeyAdded)
            updatedContent.append(id).append(" <---> ").append(password).append(" <---> ").append(keys).append(System.lineSeparator());

        return updatedContent;
    }

    public static StringBuilder updatePassword(StringBuilder plaintextContent) {

        String line = getLineToEdit(plaintextContent);
        int lineId = Integer.parseInt(line.split(" <---> ")[0]);

        // ---GET MENU OPTION FROM USER--- //
        while(true) {
            System.out.print("\n Update menu:\n " +
                    "1. keys\n " +
                    "2. password\n " +
                    "0. exit\n ");
            String choice = scanner.nextLine();
            if(!isValidChoice(choice, "update"))
                continue;

            switch (choice.toLowerCase()) {
                case "1", "keys":
                    plaintextContent = doKeyUpdate(plaintextContent, "keys", line);
                    line = searchLine(plaintextContent, lineId);
                    break;
                case "2", "pw":
                    plaintextContent = doKeyUpdate(plaintextContent, "password", line);
                    line = searchLine(plaintextContent, lineId);
                    break;
                case "0", "exit":
                    return plaintextContent;
                default:
            }
        }
    }

    public static void showAll(StringBuilder plaintextContent) {

        // ---ITERATE AND PRINT ALL LINES IN THE FILE--- //
        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        for(String line : lines)
            System.out.print("\n " + line);
        System.out.print("\n ");
        // ---WAIT FOR A DUMMY INPUT FROM USER TO TERMINATE--- //
        scanner.nextLine();
    }

    public static StringBuilder deleteKey(StringBuilder plaintextContent) {

        String line = getLineToEdit(plaintextContent);

        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        StringBuilder updatedContent = new StringBuilder();
        for (String row : lines) {
            if (!row.equals(line)) {
                updatedContent.append(row).append(System.lineSeparator());
            } else {
                System.out.print("\n INFO: Password deleted!");
            }
        }
        return updatedContent;
    }
}
