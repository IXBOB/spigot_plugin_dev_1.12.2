package com.ixbob.myplugin.util;

import org.bukkit.util.Vector;

public class Mth {
    public static double vec3ToYaw(Vector vector) {
        return Math.toDegrees(Math.acos(vector.getZ()))*(-vector.getX() / Math.abs(vector.getX()));
    }

    /**
     *
     * @param from: smaller int
     * @param to: bigger int
     * @return int
     */
    public static int randomInt(int from, int to) {
        return (int) Math.floor(Math.random() * ( to - from + 1)) + from;
    }
}
