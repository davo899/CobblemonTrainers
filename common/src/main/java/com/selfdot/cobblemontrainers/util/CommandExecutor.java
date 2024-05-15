package com.selfdot.cobblemontrainers.util;

public enum CommandExecutor {

    PLAYER,
    CONSOLE;

    public static CommandExecutor fromString(String text) {
        for (CommandExecutor commandExecutor : CommandExecutor.values()) {
            if (commandExecutor.name().equalsIgnoreCase(text)) {
                return commandExecutor;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
