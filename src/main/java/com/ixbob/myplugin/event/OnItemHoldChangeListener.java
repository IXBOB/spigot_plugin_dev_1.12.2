package com.ixbob.myplugin.event;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.rmi.server.ExportException;

public class OnItemHoldChangeListener implements Listener {
    @EventHandler
    public void onItemHoldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int newSlotNum = event.getNewSlot();
        ItemStack newSlotItem = event.getPlayer().getInventory().getItem(newSlotNum);
        if (newSlotItem == null) {
            player.setExp(0.0f);

        }
        if (newSlotItem != null) {
            if (newSlotItem.getType() == Material.WOOD_HOE) {
                NBTItem nbti = new NBTItem(newSlotItem);
                float currentExp = nbti.getFloat("cooldown_progress");
                player.setExp(currentExp);
            }
        }
    }
}
