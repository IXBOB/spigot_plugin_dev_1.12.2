package com.ixbob.myplugin.task;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TestTask extends BukkitRunnable {
    private final ItemStack eventInteractItem;

    public TestTask(ItemStack eventInteractItem) {
        this.eventInteractItem = eventInteractItem;
    }
    @Override
    public void run() {
        if (eventInteractItem.getAmount() == 32) {
            cancel();
        }
        eventInteractItem.setAmount(1);
    }
}
