package com.ixbob.myplugin.event;

import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.task.PlayerSneakToHelpCountDowner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

public class PlayerSneakingListener implements Listener {
    private static final Plugin plugin = Main.getInstance();
    @EventHandler
    public void playerSneakingListener(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()
                && !player.getMetadata("needHelpToRespawn").get(0).asBoolean()
                && !player.getMetadata("status_died").get(0).asBoolean()) {
            PlayerSneakToHelpCountDowner sneakCountDowner = new PlayerSneakToHelpCountDowner(event.getPlayer());
            int taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sneakCountDowner, 0, 1);
            Bukkit.broadcastMessage(String.valueOf(taskID));
            sneakCountDowner.setTaskID(taskID);
        }
    }
}
