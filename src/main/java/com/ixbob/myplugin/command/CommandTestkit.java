
package com.ixbob.myplugin.command;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

            ItemStack item_wood_hoe = new ItemStack(Material.WOOD_HOE);
            NBTItem nbti_wood_hoe = new NBTItem(item_wood_hoe);
            nbti_wood_hoe.setString("item_type", "gun");
            nbti_wood_hoe.setString("gun_name", "shou_qiang");
            nbti_wood_hoe.setFloat("cooldown_progress", 1.0f);
            item_wood_hoe = nbti_wood_hoe.getItem();

            ItemStack item_stone_hoe = new ItemStack(Material.STONE_HOE);
            NBTItem nbti_stone_hoe = new NBTItem(item_stone_hoe);
            nbti_stone_hoe.setString("item_type", "gun");
            nbti_stone_hoe.setString("gun_name", "bu_qiang");
            nbti_stone_hoe.setFloat("cooldown_progress", 1.0f);
            item_stone_hoe = nbti_stone_hoe.getItem();

            player.getInventory().setItem(1, item_wood_hoe);
            player.getInventory().setItem(2, item_stone_hoe);

            player.setMetadata("shou_qiang_ammo", new FixedMetadataValue(plugin, 300));
            player.setMetadata("bu_qiang_ammo", new FixedMetadataValue(plugin, 300));
        }

        return true;
    }
}