
package com.ixbob.myplugin.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class CommandKit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Location Location = new Location(Bukkit.getWorld("world"), 0, 100 ,0);
            player.teleport(Location);

//            Player player = (Player) commandSender;
//            ItemStack diamond = new ItemStack(Material.DIAMOND);
//            ItemStack bricks = new ItemStack(Material.BRICK, 20);
//            player.getInventory().addItem(bricks, diamond);
        }

        return true;
    }
}