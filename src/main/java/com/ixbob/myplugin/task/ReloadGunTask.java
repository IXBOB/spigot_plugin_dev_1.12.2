package com.ixbob.myplugin.task;

import com.ixbob.myplugin.event.OnUseHoeListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ReloadGunTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final ItemStack item;
    private final OnUseHoeListener listener;
    private final Player player;

    public ReloadGunTask(JavaPlugin plugin, ItemStack item, Player player, OnUseHoeListener listener) {
        this.plugin = plugin;
        this.item = item;
        this.listener = listener;
        this.player = player;
    }

    @Override
    public void run() {
        if (item.getDurability() > 0){
            listener.reloadGunAmmo(plugin, item, player);
        }
    }
}
