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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class ZombieMoveTask extends BukkitRunnable {
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private MongoDB mongoDB = new MongoDB("windowLoc");
    @Override
    public void run() {
        for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity.getType() == EntityType.ZOMBIE) {
                Zombie zombie = (Zombie) entity;
                if (zombie.getTicksLived() < 2) {
                    setInitMove(zombie);
                }
                setZombieVelocity(zombie);


            }
        }
    }

    private void setZombieVelocity(Zombie zombie) {
//        Vector velocity = new Vector(0.1,0,0.1);
//        zombie.setVelocity(velocity);
    }

    private void setInitMove(Zombie zombie) {
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
        System.out.println(windowLocNearest);
    }
}
