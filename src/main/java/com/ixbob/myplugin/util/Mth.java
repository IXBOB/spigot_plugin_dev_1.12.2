package com.ixbob.myplugin.util;

import org.bukkit.util.Vector;

public class Mth {
    public static double vec3ToYaw(Vector vector) {
        return Math.toDegrees(Math.acos(vector.getZ()))*(-vector.getX() / Math.abs(vector.getX()));
    }
}
