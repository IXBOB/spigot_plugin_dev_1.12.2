package com.ixbob.myplugin.task;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.ixbob.myplugin.MongoDB;
import com.ixbob.myplugin.util.Mth;
import com.ixbob.myplugin.util.Utils;
import com.mongodb.Mongo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class ZombieMoveTask extends BukkitRunnable {
    private final Plugin plugin;

    public ZombieMoveTask (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity.getType() == EntityType.ZOMBIE) {
                Zombie zombie = (Zombie) entity;
                Location location = zombie.getEyeLocation();

                if(zombie.getMetadata("onTheRoadToWindow").get(0).asBoolean()) {
                    System.out.println("run");
                    double distance = zombie.getEyeLocation().distance(Utils.getNearestWindowLoc(location));
                    boolean set_move = true;
                    if (zombie.getMetadata("frontAreaContainWoodStep").get(0).asBoolean()) {
                        zombie.setAI(false);
                        set_move = false;
                        System.out.println("close AI");
                    }
                    System.out.println(distance);
                    System.out.println(zombie.getEyeLocation().add(zombie.getLocation().getDirection()).distance(Utils.getNearestWindowLoc(location)));
                    if (distance < 1 && zombie.getEyeLocation().add(zombie.getLocation().getDirection()).distance(Utils.getNearestWindowLoc(location)) > 0.7 ) {
                        Vector moveVec3 = new Vector(zombie.getMetadata("moveVec3_x").get(0).asDouble(), 0, zombie.getMetadata("moveVec3_z").get(0).asDouble());
                        Location locationNew = new Location(zombie.getWorld(), location.getX(), zombie.getLocation().getY(), location.getZ(), (float) Mth.vec3ToYaw(moveVec3), 0);
                        zombie.teleport(locationNew);
                        System.out.println("adjust pos");
                    }
                    if (distance < 0.4 && !zombie.getMetadata("frontAreaContainWoodStep").get(0).asBoolean()) {
                        zombie.setMetadata("onTheRoadToWindow", new FixedMetadataValue(plugin, false));
                        System.out.println("stop");
                        set_move = false;
                        zombie.setAI(true);
                    }
                    if (set_move) {
                        zombie.setAI(true);
                        Vector moveVec3 = new Vector(zombie.getMetadata("moveVec3_x").get(0).asDouble(), 0, zombie.getMetadata("moveVec3_z").get(0).asDouble());
                        setVelocity(zombie, moveVec3);
                        System.out.println("set move");
                    }
                    System.out.println("============");
                }
            }
        }
    }

    private void setVelocity(Zombie zombie, Vector moveVec3) {
        zombie.setVelocity(moveVec3.multiply(0.1f));
    }
}
