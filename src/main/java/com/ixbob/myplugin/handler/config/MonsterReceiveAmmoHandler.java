package com.ixbob.myplugin.handler.config;

import com.ixbob.myplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.stream.Collectors;

public class MonsterReceiveAmmoHandler implements Runnable{
    @Override
    public void run() {
        for (LivingEntity entity : Bukkit.getWorlds().get(0).getEntities().stream()
                .filter(entity -> entity instanceof LivingEntity && entity.hasMetadata("custom_monster"))
                .filter(entity -> entity.getMetadata("custom_monster").get(0).asBoolean())
                .map(entity -> (LivingEntity) entity)
                .collect(Collectors.toList())) {
            int damageAmount = entity.getMetadata("receiving_damage_once").get(0).asInt();
            if (damageAmount != 0) {
                entity.damage(damageAmount);
            }
            entity.setMetadata("receiving_damage_once", new FixedMetadataValue(Main.getInstance(), 0));
        }
    }
}
