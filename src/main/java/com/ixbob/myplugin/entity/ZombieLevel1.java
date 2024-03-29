package com.ixbob.myplugin.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ZombieLevel1 {
    private final Location location;
    private final Plugin plugin;

    public ZombieLevel1(Location location, Plugin plugin) {
        this.location = location;
        this.plugin = plugin;
    }

    public void spawn(World world) {
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
        zombie.setMetadata("level", new FixedMetadataValue(plugin, 1));
        Bukkit.broadcastMessage("Zombie has spawned!");
    }
}
