package com.dde.utils;

import com.dde.entities.Properties;

import java.util.Scanner;

import static com.dde.Main.scanner;

public class BusinessLogic {

    public static boolean isValidChoice(String choice, String menu) {
        String[] menuOptions = (menu.equals("main") ? Properties.getMainMenuOptions().split(",") : Properties.getUpdateKeyMenuOptions().split(","));
        for(String option : menuOptions) {
            if(option.equalsIgnoreCase(choice))
                return true;
        }
        return false;
    }

    public static String getLineToEdit(StringBuilder plainTextContent) {

        // ---GET ID FROM USER--- //
        int id;
        while(true) {
            System.out.print("\n Password id: ");
            while (!scanner.hasNextInt()) scanner.next();
            id = scanner.nextInt();
            scanner.nextLine();
            if(isValidId(id, plainTextContent))
                break;
        }

        // ---SEARCH THE ID BETWEEN FILE ENTRIES--- //
        String[] lines = plainTextContent.toString().split(System.lineSeparator());
        boolean skipHeader = true;
        for(String row : lines) {
            // ---SKIP FIRST LINE--- //
            if(skipHeader) {
                skipHeader = false;
                continue;
            }

            int theId = Integer.parseInt(row.split(" <---> ")[0]);
            if(id == theId)
                return row;
        }
        // ---UNREACHABLE, INPUT ID IS VALIDATED--- //
        return null;
    }

    public static boolean isValidId(int id, StringBuilder plainTextContent) {
        boolean skipHeader = true;
        String[] lines = plainTextContent.toString().split(System.lineSeparator());
        for(String line : lines) {
            // ---SKIP FIRST LINE--- //
            if(skipHeader) {
                skipHeader = false;
                continue;
            }
            int theId = Integer.parseInt(line.split(" <---> ")[0]);
            if(theId == id)
                return true;
        }
        return false;
    }

    public static int getId(StringBuilder plaintextContent) {
        // ---STARTING ID IN THE FILE--- //
        int counter = 1;
        int previousId = 0;
        int currentId = 0;

        // ---COUNTING EXISTING LINES--- //
        boolean skipHeader = true;
        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        for(String line : lines) {
            // ---SKIP FIRST LINE--- //
            if(skipHeader) {
                skipHeader = false;
                continue;
            }
            currentId = Integer.parseInt(line.split(" <---> ")[0]);

            if(currentId - previousId > 1)
                return previousId + 1;

            counter++;
            //previousId = Integer.parseInt(line.split(" <---> ")[0]);
            previousId = currentId;
        }
        return counter;
    }

    public static StringBuilder doKeyUpdate(StringBuilder plaintextContent, String choice, String line) {

        // ---SPLIT LINE INTO COMPONENTS--- //
        int lineId = Integer.parseInt(line.split(" <---> ")[0]);
        String linePassword = line.split(" <---> ")[1];
        String lineKeys = line.split(" <---> ")[2];

        // ---REGARDING CHOICE OVERRIDE SPECIFIC COMPONENT--- //
        if (choice.equals("keys")) {
            System.out.print("\n Rewrite the entire tag section! Initial version:\n " + lineKeys + "\n\n ");
            lineKeys = scanner.nextLine();
        } else if (choice.equals("password")) {
            System.out.print("\n Rewrite the password! Initial version:\n " + linePassword + "\n\n ");
            linePassword = scanner.nextLine();
        }

        // ---REBUILD THE LINE--- //
        StringBuilder updatedLine = new StringBuilder();
        updatedLine.append(lineId).append(" <---> ").append(linePassword).append(" <---> ").append(lineKeys).append(System.lineSeparator());

        // ---OVERRIDE LINE IN FILE--- //
        String[] lines = plaintextContent.toString().split(System.lineSeparator());
        StringBuilder updatedContent = new StringBuilder();
        for (String row : lines) {
            if (!row.equals(line)) {
                updatedContent.append(row).append(System.lineSeparator());
            } else {
                updatedContent.append(updatedLine);
            }
        }
        return updatedContent;
    }
}
