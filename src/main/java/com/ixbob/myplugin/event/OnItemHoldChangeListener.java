package com.ixbob.myplugin.event;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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
            NBTItem nbtNewSlotItem = new NBTItem(newSlotItem);
            if (Objects.equals(nbtNewSlotItem.getString("item_type"), "gun")) {
                float currentExp = nbtNewSlotItem.getFloat("cooldown_progress");
                player.setExp(currentExp);
                int gun_ammo;
                switch (nbtNewSlotItem.getString("gun_name")) {
                    case ("shou_qiang"): {
                        gun_ammo = player.getMetadata("shou_qiang_ammo").get(0).asInt();
                        break;
                    }
                    case ("bu_qiang"): {
                        gun_ammo = player.getMetadata("bu_qiang_ammo").get(0).asInt();
                        break;
                    }
                    default: {
                        throw new NullPointerException("Are you kidding me? no gun matches.");
                    }
                }
                player.setLevel(gun_ammo);
            }
            else {
                player.setExp(0.0f);
            }
        }
    }
}
