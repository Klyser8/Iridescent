package com.github.klyser8.iridescent.file;

public enum Message {

    ERROR_PERMISSION("&4You don't have the permission to run this command!"),

    ERROR_INVALID_HEX("&cPlease provide a correct hex color code."),
    ERROR_INVALID_TAG("&cPlease provide a correct color tag. A color tag has to be 13 or less characters long, using only letters and numbers."),
    ERROR_EXISTING_TAG("&cThe tag you have written already exists."),

    ERROR_MISSING_TAG("&cThe tag you provided does not exist."),

    TAG_CREATION("&fYou have successfully created <tag>!"),
    TAG_DELETION("&6You have deleted &f<tag>."),

    HELP_TAG_RELOAD("&9/iridescent reload &7-&r reloads the plugin's config.yml"),
    HELP_TAG_CREATE("&9/iridescent create [hexcode] [tag] &7-&r creates a new color tag"),
    HELP_TAG_DELETE("&9/iridescent delete [tag] &7-&r deletes an existing color tag"),
    HELP_TAG_LIST("&9/iridescent list [page] &7-&r displays a list of existing color tags");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    public String getDefaultMessage() {
        return message;
    }
}
