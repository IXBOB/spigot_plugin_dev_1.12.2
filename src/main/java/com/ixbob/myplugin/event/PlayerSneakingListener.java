package com.ixbob.myplugin.event;

import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.task.PlayerSneakToHelpCountDowner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.stream.Stream;

public class PlayerSneakingListener implements Listener {
    private static final Plugin plugin = Main.getInstance();
    @EventHandler
    public void playerSneakingListener(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()
                && !player.getMetadata("needHelpToRespawn").get(0).asBoolean()
                && !player.getMetadata("status_died").get(0).asBoolean()) {
            Stream<Entity> diedPlayerStream = player.getNearbyEntities(2, 2, 2)
                    .stream()
                    .filter(entity -> (entity instanceof Player
                            && entity.getMetadata("needHelpToRespawn").get(0).asBoolean()));
            Optional<Entity> first = diedPlayerStream.findFirst();
            if (first.isPresent()) { //检查存在
                Player getHelpedPlayer = (Player) first.get();

                PlayerSneakToHelpCountDowner sneakCountDowner = new PlayerSneakToHelpCountDowner(event.getPlayer(), getHelpedPlayer);
                int taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sneakCountDowner, 0, 1);
                sneakCountDowner.setTaskID(taskID);
            }
        }
    }
}
