package com.ixbob.myplugin.task;

import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class PlayerRespawnCountDowner implements Runnable {
    private float timeLeft;
    private final Player player;
    private final ArmorStand text1Stand;
    private final ArmorStand text2Stand;
    private final Location tpLoc;
    private int taskID;
    public PlayerRespawnCountDowner(Player player, ArmorStand text1Stand, ArmorStand text2Stand, Location deathLoc) {
        timeLeft = 20.0f;
        this.player = player;
        this.text1Stand = text1Stand;
        this.text2Stand = text2Stand;
        this.tpLoc = Utils.getGround(new Location(Bukkit.getWorlds().get(0), deathLoc.getX(), deathLoc.getY(), deathLoc.getZ(), 0, 90)).add(0, 1.5, 0);
        System.out.println(deathLoc);
        System.out.println(tpLoc);
    }
    @Override
    public void run() { //run every 2 ticks
        player.teleport(tpLoc);
        if (!player.getMetadata("isBeingHelped").get(0).asBoolean()) {
            timeLeft -= 0.1f;
            player.setMetadata("respawnTimeLeft", new FixedMetadataValue(Main.plugin, timeLeft));
            text2Stand.setCustomName(String.format(LangLoader.get("player_help_respawn_time_left_line2"),
                    String.format("%.1f", timeLeft)));
            if (timeLeft <= 0.0f) {
                text1Stand.remove();
                text2Stand.setCustomName(LangLoader.get("player_died_armorstand_text"));
                player.setMetadata("status_died", new FixedMetadataValue(Main.getInstance(), true));
                player.setMetadata("needHelpToRespawn", new FixedMetadataValue(Main.getInstance(), false));
                player.sendMessage(LangLoader.get("player_died_chat_text"));
                Bukkit.getScheduler().cancelTask(taskID);
            }
            if (player.getMetadata("justRespawned").get(0).asBoolean()) {
                player.setMetadata("justRespawned", new FixedMetadataValue(Main.getInstance(), false));
                cancel();
            }
        }
    }

    public void setTaskID(int id) {
        this.taskID = id;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
