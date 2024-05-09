package com.ixbob.myplugin.entity;

import com.ixbob.myplugin.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class ZombieLevel1 {
    private final Location location;
    private final Plugin plugin;
    private Zombie zombie;

    public ZombieLevel1(Location location, Plugin plugin) {
        this.location = location;
        this.plugin = plugin;
    }

    public void spawn() {
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        this.zombie = zombie;
        zombie.setBaby(false);
        initMetaData();
        initArmor();
    }

    private void initMetaData() {
        zombie.setMetadata("custom_monster", new FixedMetadataValue(plugin, true));
        zombie.setMetadata("level", new FixedMetadataValue(plugin, 1));
    }

    private void initArmor() {
        EntityEquipment equipment = zombie.getEquipment();
        equipment.setHelmet(new ItemStack(Material.GLASS, 1));
        Utils.randomArmor(1, zombie);
    }

    public Zombie getEntity() {
        return zombie;
    }
}
