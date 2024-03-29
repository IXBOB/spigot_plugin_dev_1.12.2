package com.ixbob.myplugin.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SetZombieVelocityTask extends BukkitRunnable {
    @Override
    public void run() {
        for(Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity.getType() == EntityType.ZOMBIE) {
                LivingEntity zombie = (LivingEntity) entity;
                setZombieVelocity(zombie);
            }
        }
    }

    private void setZombieVelocity(LivingEntity zombie) {
        Vector velocity = new Vector(0.1,0,0.1);
        zombie.setVelocity(velocity);
    }
}
