package com.ixbob.myplugin.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class ZombiePigmanLevel1 {
    private final Location location;
    private final Plugin plugin;
    private PigZombie pigzombie;
    public ZombiePigmanLevel1(Location location, Plugin plugin) {
        this.location = location;
        this.plugin = plugin;
    }

    public void spawn(){
        PigZombie pigzombie = (PigZombie) location.getWorld().spawnEntity(location, EntityType.PIG_ZOMBIE);
        this.pigzombie = pigzombie;
        pigzombie.setBaby(false);
        initMetaData();
        initArmor();
        Bukkit.broadcastMessage("Zombie_Pigman has spawned!");
    }
    public void initMetaData(){
        pigzombie.setBaby(false);
        pigzombie.setMetadata("custom_monster", new FixedMetadataValue(plugin, true));
        pigzombie.setMetadata("level", new FixedMetadataValue(plugin, 1));
        pigzombie.setMetadata("receiving_damage_once", new FixedMetadataValue(plugin, 0));
    }
    public void initArmor(){
        pigzombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_BLOCK, 1));
        pigzombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
    }
    public PigZombie getEntity() {
        return pigzombie;
    }
}
