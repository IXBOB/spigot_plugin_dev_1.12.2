package com.ixbob.myplugin.event;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Objects;

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
                String damageBelongGunType = player.getMetadata("last_damage_using_gun_type").get(0).asString();
                if (Objects.equals(damageBelongGunType, "empty")
                        && player.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
                    Scoreboard scoreboard = player.getScoreboard();
                    Objective scoreboardObjective = scoreboard.getObjective("main");
                    scoreboardObjective.getScoreboard().resetScores(player.getDisplayName() + " " + ChatColor.GOLD + player.getMetadata("coin_count").get(0).asInt());
                    int playerCoinCount = player.getMetadata("coin_count").get(0).asInt();
                    player.setMetadata("coin_count",new FixedMetadataValue(plugin, playerCoinCount + GunProperties.swordHitGetCoin));
                    String message = String.format(LangLoader.get("game_hit_monster_sword"), GunProperties.swordHitGetCoin);
                    player.sendMessage(ChatColor.GOLD + message);
                    Utils.updatePlayerCoinScoreboard(player);
                }
            }
        }
    }

}
