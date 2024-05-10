package com.ixbob.myplugin.handler.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WindowAreaLoader {

    public static HashMap<Integer, List<List<Integer>>> windowAreaMap = new HashMap<>();

    public static void init (Plugin plugin) {
        plugin.saveResource("settings/window_data.yml", true);
        File languageFile = new File(plugin.getDataFolder(), "settings/window_data.yml");
        FileConfiguration windowData = YamlConfiguration.loadConfiguration(languageFile.getAbsoluteFile());
        for (int i = 1; i <= windowData.getInt("amount"); i++) {
            List<Integer> fromList = windowData.getIntegerList(i + ".from");
            List<Integer> fillList = windowData.getIntegerList(i + ".fill");
            List<List<Integer>> dataList = new ArrayList<>();
            dataList.add(fromList);
            dataList.add(fillList);
            windowAreaMap.put(i, dataList);
        }
    }

    public static void fixAllWindow() {
        for (int i = 1; i <= windowAreaMap.size(); i++) {
            List<List<Integer>> dataList = windowAreaMap.get(i);
            List<Integer> fromList = dataList.get(0);
            List<Integer> fillList = dataList.get(1);
            int fromX = fromList.get(0);
            int fromY = fromList.get(1);
            int fromZ = fromList.get(2);
            int fillX = fillList.get(0);
            int fillY = fillList.get(1);
            int fillZ = fillList.get(2);
            int placeX;
            int placeY;
            int placeZ;
            if (fillX == 0) {
                placeY = fromY;
                for (int y = Math.abs(fromY); y <= Math.abs(fromY) + Math.abs(fillY); y++) {
                    placeZ = fromZ;
                    for (int z = Math.abs(fromZ); z <= Math.abs(fromZ) + Math.abs(fillZ); z++) {
                        placeBlock(fromX, placeY, placeZ);
                        placeZ += (int) Math.signum(fillZ);
                    }
                    placeY += (int) Math.signum(fillY);
                }
            }
            if (fillZ == 0) {
                placeY = fromY;
                for (int y = Math.abs(fromY); y <= Math.abs(fromY) + Math.abs(fillY); y++) {
                    placeX = fromX;
                    for (int x = Math.abs(fromX); x <= Math.abs(fromX) + Math.abs(fillX); x++) {
                        placeBlock(placeX, placeY, fromZ);
                        placeX += (int) Math.signum(fillX);
                    }
                    placeY += (int) Math.signum(fillY);
                }
            }
        }
    }

    private static void placeBlock(int x, int y, int z) {
        Bukkit.getWorlds().get(0).getBlockAt(x, y, z).setType(Material.WOOD_STEP);
    }
}
