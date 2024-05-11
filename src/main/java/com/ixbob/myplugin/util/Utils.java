package com.ixbob.myplugin.util;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.MongoDB;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.Main;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class Utils {
    private static final MongoDB dbWindowLoc = new MongoDB("windowLoc");
    public static Location getNearestWindowLoc(Location location) {
        long dbSize = dbWindowLoc.getCollectionSize();
        Vector zombieVec3 = new Vector(location.getX(), location.getY(), location.getZ());

        Location windowLocNearest = dbWindowLoc.readPos(1);
        double distanceNearest = zombieVec3.distance(new Vector(windowLocNearest.getX(), windowLocNearest.getY(), windowLocNearest.getZ()));  //init value
        for (int i = 1; i <= dbSize; i++) {
            Location windowLoc = dbWindowLoc.readPos(i);
            Vector windowVec3 = new Vector(windowLoc.getX(), windowLoc.getY(), windowLoc.getZ());
            double distance = zombieVec3.distance(windowVec3);
            if (distance < distanceNearest) {
                distanceNearest = distance;
                windowLocNearest = windowLoc;
            }
        }
        return windowLocNearest;
    }

    public static String loadJsonAsStringFromUrl(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection urlConnection = urlObject.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            return null;
        }
        return json.toString();
    }

    public static Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server.v1_12_R1." + clazz);
    }

    private void sendPacket(Object packet) throws Exception {
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket.invoke(CorpseUtil.getConnection(player), packet);
        }
    }

    public static void sendNMSPacketToAllPlayers(Packet<?> packet) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerConnection playerConnection = ((CraftPlayer) onlinePlayer).getHandle().playerConnection;
            playerConnection.sendPacket(packet);
        }
    }

    public static void sendNMSPacketsToAllPlayers(Packet<?>[] packets) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerConnection playerConnection = ((CraftPlayer) onlinePlayer).getHandle().playerConnection;
            for (int i = 0; i <= packets.length - 1; i++) {
                Packet<?> packet = packets[i];
                playerConnection.sendPacket(packet);
            }
        }
    }

    public static void sendNMSPacket(Packet<?> packet, Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(packet);
    }

    public static void sendNMSPackets(Packet<?>[] packets, Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        for (int i = 0; i <= packets.length - 1; i++) {
            Packet<?> packet = packets[i];
            playerConnection.sendPacket(packet);
        }
    }

    public static Location getGround (Location var1) {
        Location loc = new Location(var1.getWorld(), var1.getX(), var1.getY() + 1, var1.getZ(), var1.getYaw(), var1.getPitch());
        BlockIterator iterator = new BlockIterator(Bukkit.getWorlds().get(0), loc.toVector(), new Vector(0, -1, 0), 0, 15);
        while (iterator.hasNext()) {
            Block next = iterator.next();
            System.out.println(next.getType() + " " + next.getY());
            if (!next.getType().isTransparent()) {
                double y = next.getY() + 1;
                loc.setY(y);
                return loc;
            }
        }
        String errorMsg = LangLoader.get("code_throw_error_text_in_chat") + LangLoader.get("code_throw_error_no_ground_in_under_15_blocks") + loc + Arrays.toString((new RuntimeException()).getStackTrace());
        Bukkit.broadcastMessage(errorMsg);
        Bukkit.getLogger().log(Level.SEVERE, errorMsg);
        return loc;
    }

    public static boolean isAmmoHitHead(LivingEntity nearByEntity) {
        double last_damage_bullet_pos_y = nearByEntity.getMetadata("last_damage_bullet_pos_y").get(0).asDouble();
        double current_pos_y = nearByEntity.getLocation().getY();
        return Math.abs(last_damage_bullet_pos_y - (current_pos_y + 1.75)) <= 0.5;
    }

    public static void updatePlayerCoinScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective scoreboardObjective = scoreboard.getObjective("main");
        scoreboardObjective.getScore(player.getDisplayName() + " " + ChatColor.GOLD +player.getMetadata("coin_count").get(0).asInt()).setScore(0);
        player.setScoreboard(scoreboardObjective.getScoreboard());
    }

    public static void randomArmor(int armorLevel, LivingEntity entity) {
        ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
        ItemStack chainChestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
        ItemStack chainLeggings = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
        ItemStack chainBoots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
        switch (armorLevel) {
            case 1: {
                for (int i = 1; i <= 4; i++) {
                    EntityEquipment equipment = entity.getEquipment();
                    switch (Mth.randomInt(1, 10)) {
                        case 1: equipment.setChestplate(leatherChestplate); break;
                        case 2: equipment.setLeggings(leatherLeggings); break;
                        case 3: equipment.setBoots(leatherBoots); break;
                        case 4: leatherChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 5: leatherLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 6: leatherBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 7: leatherChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 8: leatherLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 9: leatherBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 10: entity.addPotionEffect(PotionEffectType.SPEED.createEffect(99999, 1)); break;
                    }
                } break;
            }
            case 2: {
                for (int i = 1; i <= 4; i++) {
                    EntityEquipment equipment = entity.getEquipment();
                    switch (Mth.randomInt(1, 10)) {
                        case 1: equipment.setChestplate(chainChestplate); break;
                        case 2: equipment.setLeggings(chainLeggings); break;
                        case 3: equipment.setBoots(chainBoots); break;
                        case 4: chainChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 5: chainLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 6: chainBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        case 7: chainChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 8: chainLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 9: chainBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        case 10: entity.addPotionEffect(PotionEffectType.SPEED.createEffect(99999, 1)); break;
                    }
                } break;
            }
        }

    }
}
