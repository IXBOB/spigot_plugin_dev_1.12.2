
package com.ixbob.myplugin.command;

import com.ixbob.myplugin.entity.ZombieLevel1;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandTest implements CommandExecutor {
    private Plugin plugin;

    public CommandTest (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Location location = player.getLocation();
            ZombieLevel1 zombieLevel1 = new ZombieLevel1(location, plugin);
            zombieLevel1.spawn(location.getWorld());
        }

        return true;
    }
}