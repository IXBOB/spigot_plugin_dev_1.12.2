package com.ixbob.myplugin.task;

import com.ixbob.myplugin.event.OnUseHoeListener;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ShotCoolDownTask extends BukkitRunnable {
    private final OnUseHoeListener listener;
    private final Player player;
    private final float addExpPerCount;
    private final ItemStack eventInteractItem;

    public ShotCoolDownTask(Player player, float addExpPerCount,ItemStack eventInteractItem, OnUseHoeListener listener) {
        this.listener = listener;
        this.player = player;
        this.addExpPerCount = addExpPerCount;
        this.eventInteractItem = eventInteractItem;
    }

    @Override
    public void run() {
        NBTItem nbtEventInteractItem = new NBTItem(eventInteractItem);
        if (nbtEventInteractItem.getFloat("cooldown_progress") < 1.0f){
            listener.shotCoolDown(player, addExpPerCount, eventInteractItem);
        }
    }
}
