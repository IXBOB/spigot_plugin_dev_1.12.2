package com.ixbob.myplugin.event;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class OnItemHeldChangeListener implements Listener {
    @EventHandler
    public void onItemHoldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int newSlotNum = event.getNewSlot();
        ItemStack newSlotItem = event.getPlayer().getInventory().getItem(newSlotNum);
        if (newSlotItem == null) {
            player.setExp(0.0f);
            player.setLevel(0);
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
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
                    case ("xiandan_qiang"): {
                        gun_ammo = player.getMetadata("xiandan_qiang_ammo").get(0).asInt();
                        break;
                    }
                    case ("dianyong_qiang"): {
                        gun_ammo = player.getMetadata("dianyong_qiang_ammo").get(0).asInt();
                        break;
                    }case ("guci"): {
                        gun_ammo = player.getMetadata("guci_ammo").get(0).asInt();
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
                player.setLevel(0);
            }

            if (newSlotItem.getType() == Material.IRON_SWORD) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 9999999, 4, false, false));
            }
            else player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }
    }
}
