package com.ixbob.myplugin.task;

import com.ixbob.myplugin.event.OnUseHoeListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ReloadGunTask extends BukkitRunnable {
    private final ItemStack item;
    private final OnUseHoeListener listener;
    private final Player player;

    public ReloadGunTask(ItemStack item, Player player, OnUseHoeListener listener) {
        this.item = item;
        this.listener = listener;
        this.player = player;
    }

    @Override
    public void run() {
        if (item.getDurability() > 0){
            listener.reloadGunAmmo(item, player);
        }
    }
}
