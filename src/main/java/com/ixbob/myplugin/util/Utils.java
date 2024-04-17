package com.ixbob.myplugin.util;

import com.ixbob.myplugin.MongoDB;
import org.bukkit.Location;
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

public class Utils {
    private static MongoDB mongoDB = new MongoDB("windowLoc");
    public static Location getNearestWindowLoc(Location location) {
        long dbSize = mongoDB.getCollectionSize();
        Vector zombieVec3 = new Vector(location.getX(), location.getY(), location.getZ());

        Location windowLocNearest = mongoDB.readPos(1);
        double distanceNearest = zombieVec3.distance(new Vector(windowLocNearest.getX(), windowLocNearest.getY(), windowLocNearest.getZ()));  //init value
        for (int i = 1; i <= dbSize; i++) {
            Location windowLoc = mongoDB.readPos(i);
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
}
