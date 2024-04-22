package com.ixbob.myplugin.util;

import com.ixbob.myplugin.MongoDB;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
}
