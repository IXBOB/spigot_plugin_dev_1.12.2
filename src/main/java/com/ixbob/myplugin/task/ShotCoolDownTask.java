package com.ixbob.myplugin.task;

import com.ixbob.myplugin.event.OnUseHoeListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ShotCoolDownTask extends BukkitRunnable {
    private final OnUseHoeListener listener;
    private final Player player;
    private final float addExpPerCount;
    private final ItemStack event_item;

    public ShotCoolDownTask(Player player, float addExpPerCount,ItemStack eventInteractItem, OnUseHoeListener listener) {
        this.listener = listener;
        this.player = player;
        this.addExpPerCount = addExpPerCount;
        this.event_item = eventInteractItem;
    }

    @Override
    public void run() {
        if (player.getExp() < 1.0f){
            listener.shotCoolDown(player, addExpPerCount, event_item);
        }
    }
}
