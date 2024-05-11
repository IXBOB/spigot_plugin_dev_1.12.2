
package com.ixbob.myplugin.command;

import com.ixbob.myplugin.GunProperties;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class CommandTestkit implements CommandExecutor {
    private final Plugin plugin;
    public CommandTestkit (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            ItemStack item_iron_sword = new ItemStack(Material.IRON_SWORD, 1);
            ItemMeta ironSwordItemMeta = item_iron_sword.getItemMeta();
            ironSwordItemMeta.setUnbreakable(true);
            item_iron_sword.setItemMeta(ironSwordItemMeta);

            ItemStack item_wood_hoe = new ItemStack(Material.WOOD_HOE, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.SHOU_QIANG));
            NBTItem nbti_wood_hoe = new NBTItem(item_wood_hoe);
            nbti_wood_hoe.setString("item_type", "gun");
            nbti_wood_hoe.setString("gun_name", "shou_qiang");
            nbti_wood_hoe.setFloat("cooldown_progress", 1.0f);
            nbti_wood_hoe.setBoolean("reloading", false);
            item_wood_hoe = nbti_wood_hoe.getItem();

            ItemStack item_stone_hoe = new ItemStack(Material.STONE_HOE, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.BU_QIANG));
            NBTItem nbti_stone_hoe = new NBTItem(item_stone_hoe);
            nbti_stone_hoe.setString("item_type", "gun");
            nbti_stone_hoe.setString("gun_name", "bu_qiang");
            nbti_stone_hoe.setFloat("cooldown_progress", 1.0f);
            nbti_stone_hoe.setBoolean("reloading", false);
            item_stone_hoe = nbti_stone_hoe.getItem();

            ItemStack item_stone_shovel = new ItemStack(Material.STONE_SPADE, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.XIANDAN_QIANG));
            NBTItem nbti_stone_shovel = new NBTItem(item_stone_shovel);
            nbti_stone_shovel.setString("item_type", "gun");
            nbti_stone_shovel.setString("gun_name", "xiandan_qiang");
            nbti_stone_shovel.setFloat("cooldown_progress", 1.0f);
            nbti_stone_shovel.setBoolean("reloading", false);
            item_stone_shovel = nbti_stone_shovel.getItem();

            ItemStack item_diamond_hoe = new ItemStack(Material.DIAMOND_HOE, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.DIANYONG_QIANG));
            NBTItem nbti_diamond_hoe = new NBTItem(item_diamond_hoe);
            nbti_diamond_hoe.setString("item_type", "gun");
            nbti_diamond_hoe.setString("gun_name", "dianyong_qiang");
            nbti_diamond_hoe.setFloat("cooldown_progress", 1.0f);
            nbti_diamond_hoe.setBoolean("reloading", false);
            item_diamond_hoe = nbti_diamond_hoe.getItem();

            player.getInventory().setItem(0, item_iron_sword);
            player.getInventory().setItem(1, item_wood_hoe);
            player.getInventory().setItem(2, item_stone_hoe);
            player.getInventory().setItem(3, item_stone_shovel);
            player.getInventory().setItem(4, item_diamond_hoe);

            player.setMetadata("shou_qiang_ammo", new FixedMetadataValue(plugin, 300));
            player.setMetadata("bu_qiang_ammo", new FixedMetadataValue(plugin, 300));
            player.setMetadata("xiandan_qiang_ammo", new FixedMetadataValue(plugin, 300));
            player.setMetadata("dianyong_qiang_ammo", new FixedMetadataValue(plugin, 300));

            player.setMetadata("shou_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.SHOU_QIANG)));
            player.setMetadata("bu_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.BU_QIANG)));
            player.setMetadata("xiandan_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.XIANDAN_QIANG)));
            player.setMetadata("dianyong_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, GunProperties.gunMagazineFullAmmo.get(GunProperties.GunType.DIANYONG_QIANG)));

            player.setMetadata("gun_name_slot_1", new FixedMetadataValue(plugin, nbti_wood_hoe.getString("gun_name")));
            player.setMetadata("gun_name_slot_2", new FixedMetadataValue(plugin, nbti_stone_hoe.getString("gun_name")));
            player.setMetadata("gun_name_slot_3", new FixedMetadataValue(plugin, nbti_stone_shovel.getString("gun_name")));
            player.setMetadata("gun_name_slot_4", new FixedMetadataValue(plugin, nbti_diamond_hoe.getString("gun_name")));
        }

        return true;
    }
}