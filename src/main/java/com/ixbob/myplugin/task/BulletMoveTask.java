package com.ixbob.myplugin.task;

import com.ixbob.myplugin.event.OnUseHoeListener;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

public class BulletMoveTask extends BukkitRunnable {
    private final OnUseHoeListener listener;
    private final ArmorStand armorStand;

    public BulletMoveTask(ArmorStand armorStand, OnUseHoeListener listener) {
        this.listener = listener;
        this.armorStand = armorStand;
    }

    @Override
    public void run() {
        listener.bulletMove(armorStand);
    }
}
