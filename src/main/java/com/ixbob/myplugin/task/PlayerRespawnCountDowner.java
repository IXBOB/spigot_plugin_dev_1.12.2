package com.ixbob.myplugin.task;

import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.handler.config.LangLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerRespawnCountDowner implements Runnable {
    private float timeLeft;
    private final Player player;
    private final ArmorStand text1Stand;
    private final ArmorStand text2Stand;
    private int taskID;
    public PlayerRespawnCountDowner(Player player, ArmorStand text1Stand, ArmorStand text2Stand) {
        timeLeft = 20.0f;
        this.player = player;
        this.text1Stand = text1Stand;
        this.text2Stand = text2Stand;
    }
    @Override
    public void run() { //run every 2 ticks
        timeLeft -= 0.1f;
        player.setMetadata("respawnTimeLeft", new FixedMetadataValue(Main.plugin, timeLeft));
        text2Stand.setCustomName(String.format(LangLoader.get("player_help_respawn_time_left_line2"),
                String.format("%.1f", timeLeft)));
        if (timeLeft <= 0.0f) {
            text1Stand.remove();
            text2Stand.remove();
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public void setTaskID(int id) {
        this.taskID = id;
    }
}
