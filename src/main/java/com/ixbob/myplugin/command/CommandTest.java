
package com.ixbob.myplugin.command;

import com.ixbob.myplugin.entity.ZombieLevel1;
import com.ixbob.myplugin.task.ReloadGunTask;
import com.ixbob.myplugin.task.TestTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Test;

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
            ItemStack item = player.getInventory().getItemInMainHand();
            BukkitTask task = new TestTask(item).runTaskTimer(plugin, 0, 1);
        }

        return true;
    }
}