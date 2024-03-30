package com.ixbob.myplugin.task;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TestTask extends BukkitRunnable {
    private final ItemStack eventInteractItem;
    private int test;

    public TestTask(ItemStack eventInteractItem) {
        this.eventInteractItem = eventInteractItem;
    }
    @Override
    public void run() {
        if (eventInteractItem.getAmount() == 32) {
            cancel();
        }
        eventInteractItem.setAmount(1);
        test += 1;
        System.out.println(test);
    }
}
