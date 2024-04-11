package com.ixbob.myplugin.util;

import com.ixbob.myplugin.MongoDB;
import org.bukkit.Location;
import org.bukkit.util.Vector;

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
}
