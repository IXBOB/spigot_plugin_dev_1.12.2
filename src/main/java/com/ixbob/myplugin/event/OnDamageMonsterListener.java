package com.ixbob.myplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class OnDamageMonsterListener implements Listener {
    private final Plugin plugin;
    public OnDamageMonsterListener (Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void OnDamageMonster(EntityDamageByEntityEvent event) {
        List<MetadataValue> isCustomMonsterMeta = event.getEntity().getMetadata("custom_monster");
        if (!isCustomMonsterMeta.isEmpty()) {
            boolean isCustomMonster = isCustomMonsterMeta.get(0).asBoolean();
            if (isCustomMonster && event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                int playerCoinCount = player.getMetadata("coin_count").get(0).asInt();
                double last_damage_bullet_pos_y = event.getEntity().getMetadata("last_damage_bullet_pos_y").get(0).asDouble();
                double current_pos_y = event.getEntity().getLocation().getY();
                System.out.println(last_damage_bullet_pos_y);
                System.out.println(current_pos_y);
                Scoreboard scoreboard = player.getScoreboard();
                Objective scoreboardObjective = scoreboard.getObjective("main");
                String a = player.getDisplayName() + " " + player.getMetadata("coin_count").get(0).asInt();
                scoreboardObjective.getScoreboard().resetScores(player.getDisplayName() + " " + ChatColor.GOLD + playerCoinCount);
                if ( Math.abs(last_damage_bullet_pos_y - (current_pos_y + 1.75)) <= 0.5 ){
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    entity.damage(3);
                    String message_add_coin_baotou = "±¬Í·ÃüÖÐ + 10 Ó²±Ò";
                    player.setMetadata("coin_count",new FixedMetadataValue(plugin, playerCoinCount + 10));
                    player.sendMessage(ChatColor.GOLD + message_add_coin_baotou);
                }
                else {
                    String message_add_coin_normal = "ÃüÖÐ + 7 Ó²±Ò";
                    player.setMetadata("coin_count",new FixedMetadataValue(plugin, playerCoinCount + 7));
                    player.sendMessage(ChatColor.GOLD + message_add_coin_normal);
                }
                scoreboardObjective.getScore(player.getDisplayName() + " " + ChatColor.GOLD +player.getMetadata("coin_count").get(0).asInt()).setScore(0);
                player.setScoreboard(scoreboardObjective.getScoreboard());
            }
        }
    }
}
