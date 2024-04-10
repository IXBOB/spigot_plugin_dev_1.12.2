package com.ixbob.myplugin.task;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.ixbob.myplugin.MongoDB;
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
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private MongoDB mongoDB = new MongoDB("windowLoc");
    public ZombieMoveTask (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity.getType() == EntityType.ZOMBIE) {

                Zombie zombie = (Zombie) entity;

                if (zombie.getMetadata("justSpawnedForMoveTask").get(0).asBoolean()) {
                    Location nearestWindowLoc = getNearestWindowLoc(zombie);
                    Vector moveVec3 = calculateMoveVec3(zombie, nearestWindowLoc);
                    zombie.setMetadata("moveVec3_x", new FixedMetadataValue(plugin, moveVec3.getX()));
                    zombie.setMetadata("moveVec3_z", new FixedMetadataValue(plugin, moveVec3.getZ()));
                    zombie.setMetadata("justSpawnedForMoveTask", new FixedMetadataValue(plugin, false));
                    zombie.setMetadata("waitingMoveToWindow", new FixedMetadataValue(plugin, true));
                }

                double distance = getNearestWindowLoc(zombie).distance(zombie.getLocation());
                if (distance < 0.2) {
                    zombie.setMetadata("waitingMoveToWindow", new FixedMetadataValue(plugin, false));
                    continue;
                }
                if (!zombie.getMetadata("waitingMoveToWindow").get(0).asBoolean()) {
                    continue;
                }
                setVelocity(zombie,  new Vector(zombie.getMetadata("moveVec3_x").get(0).asDouble(), 0, zombie.getMetadata("moveVec3_z").get(0).asDouble()));
            }
        }
    }

    private Vector calculateMoveVec3(Zombie zombie, Location windowLoc) {
        Location zombieLoc = zombie.getLocation();
        return new Vector(windowLoc.getX() - zombieLoc.getX(), 0, windowLoc.getZ() - zombieLoc.getZ()).normalize();
    }

    private void setVelocity(Zombie zombie, Vector moveVec3) {
        zombie.getLocation().setDirection(moveVec3); // TODO: Nothing happened (failed to set zombie's direction).
        zombie.setVelocity(moveVec3.multiply(0.1f));
    }

    private Location getNearestWindowLoc(Zombie zombie) {
        long dbSize = mongoDB.getCollectionSize();
        Location location = zombie.getLocation();
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
