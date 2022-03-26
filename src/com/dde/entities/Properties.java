package com.dde.entities;

public class Properties {
    private static String fileLocation;
    private static String cipherName;
    private static String plaintextName;
    private static String mainMenuOptions;
    private static String updateKeyMenuOptions;

    public Properties(String fileLocation, String cipherName, String plaintextName, String mainMenuOptions, String updateKeyMenuOptions) {
        Properties.fileLocation = fileLocation;
        Properties.cipherName = cipherName;
        Properties.plaintextName = plaintextName;
        Properties.mainMenuOptions = mainMenuOptions;
        Properties.updateKeyMenuOptions = updateKeyMenuOptions;
    }

    public static String getFileLocation() {
        return fileLocation;
    }

    public static String getCipherName() {
        return cipherName;
    }

    public static String getPlaintextName() {
        return plaintextName;
    }

    public static String getMainMenuOptions() {
        return mainMenuOptions;
    }

    public static String getUpdateKeyMenuOptions() {
        return updateKeyMenuOptions;
    }
}
