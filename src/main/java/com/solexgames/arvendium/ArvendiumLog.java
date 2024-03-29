package com.solexgames.arvendium;

import lombok.Getter;

public class ArvendiumLog {

    @Getter private static final ArvendiumLog instance = new ArvendiumLog();

    /**
     * Provides access to colors using ANSI escape codes.
     * Only supports normal Bash implementations.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Logs an error into the console.
     * @param message The message which the error will display.
     */
    public void error(String message) {
        System.err.println(ANSI_RED + "[ERROR] " + ANSI_WHITE + message);
    }

    /**
     * Logs info into the console.
     * @param message The message which success will display.
     */
    public void info(String message) {
        System.out.println(ANSI_GREEN + "[INFO] " + ANSI_WHITE + message);
    }

    /**
     * Logs warning into the console.
     * @param message The message which the warning will display.
     */
    public void warning(String message) {
        System.out.println(ANSI_YELLOW + "[WARN] " + ANSI_WHITE + message);
    }
}