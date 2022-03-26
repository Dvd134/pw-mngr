package com.dde.entities;

public class Properties {
    private static String fileLocation;
    private static String cipherName;
    private static String mainMenuOptions;
    private static String updateKeyMenuOptions;

    public Properties(String fileLocation, String cipherName, String mainMenuOptions, String updateKeyMenuOptions) {
        Properties.fileLocation = fileLocation;
        Properties.cipherName = cipherName;
        Properties.mainMenuOptions = mainMenuOptions;
        Properties.updateKeyMenuOptions = updateKeyMenuOptions;
    }

    public static String getFileLocation() {
        return fileLocation;
    }

    public static String getCipherName() {
        return cipherName;
    }

    public static String getMainMenuOptions() {
        return mainMenuOptions;
    }

    public static String getUpdateKeyMenuOptions() {
        return updateKeyMenuOptions;
    }
}
