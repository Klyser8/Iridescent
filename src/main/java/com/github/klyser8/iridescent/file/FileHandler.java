package com.github.klyser8.iridescent.file;

import com.github.klyser8.iridescent.Iridescent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    private Iridescent plugin;

    public FileHandler(Iridescent plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration colors;
    private File colorsFile;

    private FileConfiguration lang;
    private File langFile;

    public void setup() {
        List<String> languages = Arrays.asList("english", "italian", "spanish", "russian", "chinese", "portuguese");
        for (String language : languages) {
            if (!new File(plugin.getDataFolder().getPath() + File.separator + "lang/" + language + ".yml").exists())
                plugin.saveResource("lang/" + language + ".yml", false);
        }
        setupColorsFile();
        setupLanguage();
    }

    private void setupColorsFile() {
        colorsFile = new File(plugin.getDataFolder(), "colors.yml");
        if (!colorsFile.exists()) {
            colorsFile.getParentFile().mkdirs();
            plugin.saveResource("colors.yml", false);
        }

        colors = new YamlConfiguration();
        try {
            colors.load(colorsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void setupLanguage() {
        String languageFile;
        plugin.setLanguage(plugin.getConfig().getString("Language").toLowerCase());
        if (plugin.getDebugLevel() >= 1) plugin.getLogger().info("- [Plugin language]: " + plugin.getLanguage());
        switch (plugin.getLanguage().toLowerCase()) {
            default: languageFile = "english.yml";
                break;
            case "italian": languageFile = "italian.yml";
                break;
            case "spanish": languageFile = "spanish.yml";
                break;
            case "russian": languageFile = "russian.yml";
                break;
            case "chinese": languageFile = "chinese.yml";
                break;
            case "portuguese": languageFile = "portuguese.yml";
                break;
        }
        langFile = new File(plugin.getDataFolder() + File.separator + "lang", languageFile);
        lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getLang() {
        if (lang == null) {
            Bukkit.getServer().getLogger().severe("Could not retrieve " + plugin.getLanguage() + ".yml!");
            return null;
        } else {
            return lang;
        }
    }

    public FileConfiguration getColors() {
        if (colors == null) {
            Bukkit.getServer().getLogger().severe("Could not retrieve colors.yml!");
            return null;
        } else {
            return colors;
        }
    }

    public void saveColors() {
        try {
            colors.save(colorsFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save colors.yml!");
        }
    }

    public void reloadColors() {
        colors = YamlConfiguration.loadConfiguration(colorsFile);
    }

}