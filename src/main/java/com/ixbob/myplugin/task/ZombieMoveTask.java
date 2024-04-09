package com.ixbob.myplugin.task;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ZombieMoveTask extends BukkitRunnable {
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
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
//        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
//        packet.getEntityModifier(Bukkit.getWorld("world")).write()
    }

    private void setInitMove(Zombie zombie) {

    }
}
