package com.ixbob.myplugin.handler.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class LangLoader {

    public static HashMap<String, String> translationMap = new HashMap<>();

    public static void init (Plugin plugin) {
        plugin.saveResource("languages/zh_CN.yml", true);
        File languageFile = new File(plugin.getDataFolder(), "languages/zh_CN.yml");
        FileConfiguration translations = YamlConfiguration.loadConfiguration(languageFile.getAbsoluteFile());
        for (String translation : translations.getKeys(false)) {
            translationMap.put(translation, translations.getString(translation));
        }
    }

    public static String get(String key) {
        return translationMap.get(key);
    }
}
