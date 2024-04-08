package com.ixbob.myplugin.task;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.event.OnUseHoeListener;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ReloadGunTask extends BukkitRunnable {
    private ItemStack item;
    private final OnUseHoeListener listener;
    private final Player player;
    private final Plugin plugin;
    private final Map<GunProperties.GunType, Float> gunReloadAmmoTime;
    private final Map<GunProperties.GunType, Integer> gunDurabilityLegacy;
    private final Map<GunProperties.GunType, Integer> gunMagazineFullAmmo;

    private float reloadSpentTime = 0.0f;

    public ReloadGunTask(ItemStack item, Player player, OnUseHoeListener listener,
                         Map<GunProperties.GunType, Float> gunReloadAmmoTime,
                         Map<GunProperties.GunType, Integer> gunDurabilityLegacy,
                         Map<GunProperties.GunType, Integer> gunMagazineFullAmmo,
                         Plugin plugin) {
        this.item = item;
        this.listener = listener;
        this.player = player;
        this.plugin = plugin;
        this.gunReloadAmmoTime = gunReloadAmmoTime;
        this.gunDurabilityLegacy = gunDurabilityLegacy;
        this.gunMagazineFullAmmo = gunMagazineFullAmmo;
    }

    @Override
    public void run() {
        if (item.getDurability() > 0){
            System.out.println("reloading");
            System.out.println("reload_time: " + reloadSpentTime);
            NBTItem nbti_cache = new NBTItem(item);
            String usingGunName = nbti_cache.getString("gun_name");
            GunProperties.GunType usingGunTypeInstance = GunProperties.GunType.valueOf(usingGunName.toUpperCase());
            short newDurability = (short) ( gunDurabilityLegacy.get(usingGunTypeInstance) - reloadSpentTime / gunReloadAmmoTime.get(usingGunTypeInstance) * gunDurabilityLegacy.get(usingGunTypeInstance) );
            System.out.println("newDurability: " + newDurability);
            item = nbti_cache.getItem();
            item.setDurability(newDurability);
            NBTItem nbti = new NBTItem(item);
            nbti.setFloat("cooldown_progress", 1.0f);
            nbti.setBoolean("reloading", true);
            item = nbti.getItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(itemMeta);
            short durability_new = item.getDurability();
            if (durability_new == 0) {
                System.out.println("set_false");
                nbti.setBoolean("reloading", false);
                item = nbti.getItem();
                ItemMeta itemMeta_finish = item.getItemMeta();
                itemMeta_finish.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(itemMeta_finish);
                player.setExp(1.0f);

                player.setMetadata(usingGunTypeInstance.getPlayerMagazineAmmoMetadataKey(), new FixedMetadataValue(plugin, gunMagazineFullAmmo.get(usingGunTypeInstance).shortValue()));
                item.setAmount(gunMagazineFullAmmo.get(usingGunTypeInstance));
//                switch (nbti.getString("gun_name")) {
//                    case ("shou_qiang"): {
//                        player.setMetadata("shou_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, 30));
//                        item.setAmount(gunMagazineFullAmmo.get("shou_qiang"));
//                        break;
//                    }
//                    case ("bu_qiang"): {
//                        player.setMetadata("bu_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, 50));
//                        item.setAmount(gunMagazineFullAmmo.get("bu_qiang"));
//                        break;
//                    }
//                    default:
//                        throw new NullPointerException("Are you kidding me? no gun matches.");
//                }
                cancel();
            }
            switch (nbti.getString("gun_name")) {
                case ("shou_qiang"): {
                    player.getInventory().setItem(1, item);
                    break;
                }
                case ("bu_qiang"): {
                    player.getInventory().setItem(2, item);
                    break;
                }
                default:
                    throw new NullPointerException("Are you kidding me? no gun matches.");
            }
            reloadSpentTime += 0.05f;
        }
    }
}
