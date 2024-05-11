package com.ixbob.myplugin.task;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

import static com.ixbob.myplugin.GunProperties.gunBulletMoveDistance;
import static com.ixbob.myplugin.GunProperties.gunDamage;

public class BulletMoveTask implements Runnable {
    private final ArmorStand armorStand;
    public static final Plugin plugin = com.ixbob.myplugin.Main.getInstance();
    private int taskID;

    public BulletMoveTask(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    @Override
    public void run() {
        GunProperties.GunType belongGunType = GunProperties.getGunTypeByString(armorStand.getMetadata("belong_gun_type").get(0).asString());
        float bulletSpeed = GunProperties.gunBulletMoveSpeed.get(belongGunType);
        armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, armorStand.getMetadata("fly_distance").get(0).asFloat() + bulletSpeed));
        List<Entity> nearbyEntities = armorStand.getNearbyEntities(0.1,0.1,0.1);
        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity.getType() != EntityType.DROPPED_ITEM
                        && entity.getType() != EntityType.ARROW
                        && entity.getType() != EntityType.LINGERING_POTION
                        && entity.getType() != EntityType.SPLASH_POTION
                        && entity.getType() != EntityType.EXPERIENCE_ORB) {
                    LivingEntity nearbyEntity = (LivingEntity) nearbyEntities.get(0);
                    if (!nearbyEntity.isDead() && nearbyEntity.getType() == EntityType.ZOMBIE
                            || nearbyEntity.getType() == EntityType.SKELETON
                            || nearbyEntity.getType() == EntityType.CREEPER
                            || nearbyEntity.getType() == EntityType.SPIDER
                            || nearbyEntity.getType() == EntityType.PIG_ZOMBIE) {
                        boolean hitHead = false;
                        nearbyEntity.setMetadata("last_damage_bullet_pos_y", new FixedMetadataValue(plugin, armorStand.getLocation().getY()));
                        nearbyEntity.setMetadata("receiving_damage_once", new FixedMetadataValue(plugin, nearbyEntity.getMetadata("receiving_damage_once").get(0).asInt() + gunDamage.get(belongGunType)));
                        if (Utils.isAmmoHitHead(nearbyEntity)) {
                            nearbyEntity.setMetadata("receiving_damage_once", new FixedMetadataValue(plugin, nearbyEntity.getMetadata("receiving_damage_once").get(0).asInt() + 3));
                            hitHead = true;
                        }
                        Player owner = Bukkit.getPlayer(armorStand.getMetadata("owner").get(0).asString());
                        int playerCoinCount = owner.getMetadata("coin_count").get(0).asInt();
                        Scoreboard scoreboard = owner.getScoreboard();
                        Objective scoreboardObjective = scoreboard.getObjective("main");
                        scoreboardObjective.getScoreboard().resetScores(owner.getDisplayName() + " " + ChatColor.GOLD + owner.getMetadata("coin_count").get(0).asInt());
                        String message;
                        if (hitHead) {
                            owner.setMetadata("coin_count",new FixedMetadataValue(plugin, playerCoinCount + GunProperties.gunHitHeadGetCoin.get(belongGunType)));
                            message = String.format(LangLoader.get("game_hit_monster_head"), GunProperties.gunHitHeadGetCoin.get(belongGunType));
                        }
                        else {
                            owner.setMetadata("coin_count",new FixedMetadataValue(plugin, playerCoinCount + GunProperties.gunHitDefaultGetCoin.get(belongGunType)));
                            message = String.format(LangLoader.get("game_hit_monster_default"), GunProperties.gunHitDefaultGetCoin.get(belongGunType));
                        }
                        owner.sendMessage(message);
                        Utils.updatePlayerCoinScoreboard(owner);
                        armorStand.remove();
                        cancel();
                        return;
                    }
                }
            }
        }
        if (armorStand.getLocation().getBlock().getType() != Material.AIR
                || armorStand.getMetadata("fly_distance").get(0).asFloat() >= gunBulletMoveDistance.get(belongGunType)) {
            armorStand.remove();
            cancel();
            return;
        }
        armorStand.getWorld().spawnParticle(Particle.CRIT, armorStand.getLocation(),  1, 0, 0, 0, 0);
        armorStand.teleport(armorStand.getLocation().add(armorStand.getLocation().getDirection().multiply(bulletSpeed)));
    }

    public void setTaskID(int id) {
        this.taskID = id;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
