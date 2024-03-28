package com.ixbob.myplugin.event;

import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.task.ReloadGunTask;
import com.ixbob.myplugin.task.ShotCoolDownTask;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Objects;

public class OnUseHoeListener implements Listener {
    private final Main plugin;

    public OnUseHoeListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onUseWoodenHoe(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Action action = event.getAction();
        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();

        if (event.getHand() ==
                EquipmentSlot.HAND
                && item != null
                && item.getType() == Material.WOOD_HOE) {

            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    && player.getExp() == 1.0f ) {
                Vector interactDirection = player.getLocation().getDirection();
                Location interactLocation_legacy = new Location(world, player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ());
                Location interactLocation = interactLocation_legacy.add(player.getLocation().getDirection().multiply(1.3));

                Arrow arrow = world.spawnArrow(interactLocation, interactDirection, 3f, 1f);
                arrow.setPickupStatus(Arrow.PickupStatus.ALLOWED);
                player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                item.setDurability((short) (item.getDurability() + 1));
                if (item.getDurability() >= 59) {
                    reloadGunAmmo(plugin, item, player);
                }
                player.setExp(0f);
                NBTItem nbtItem = new NBTItem(item);
                nbtItem.setFloat("cooldown_progress", 0.0f);
                item = nbtItem.getItem();
                player.getInventory().setItemInMainHand(item);
                float addExpPerCount = calculateAddExpCount(1.0f);
                shotCoolDown(player, addExpPerCount, item);
            }
            if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                    && item.getDurability() != 0){
                item.setDurability((short) 59);
                reloadGunAmmo(plugin, item, player);
            }
        }
    }

    public float calculateAddExpCount (float coolDownTime) {   //Update xp bar per 0.05s (1 tick)
        float addExpCount = coolDownTime / 0.05f;  //fill it with how many times
        return 1.0f / addExpCount;  //fill how much per time
    }

    public void shotCoolDown(Player player, float addExpPerCount, ItemStack eventInteractItem) {
        NBTItem event_nbt_item = new NBTItem(eventInteractItem);
        ItemStack inhand_item = player.getInventory().getItemInMainHand();
        if (inhand_item.getType() == Material.WOOD_HOE) {
            NBTItem inhand_nbt_item = new NBTItem(inhand_item);
            String check_target_name = inhand_nbt_item.getString("gun_name");
            if (Objects.equals(check_target_name, "shou_qiang")) {
                float newExp = setNew(player.getExp() + addExpPerCount); //Prevent float from reporting errors due to accuracy
                player.setExp(newExp);
            }
        }
        event_nbt_item.setFloat("cooldown_progress", setNew(event_nbt_item.getFloat("cooldown_progress") + addExpPerCount));
        eventInteractItem = event_nbt_item.getItem();
        player.getInventory().setItem(1, eventInteractItem);
        BukkitTask task = new ShotCoolDownTask(player, addExpPerCount, eventInteractItem,this).runTaskLater(plugin, 1);
    }

    public void reloadGunAmmo(JavaPlugin plugin, ItemStack item, Player player) {
        short newDurability = (short) (item.getDurability() - 1);
        item.setDurability(newDurability);
        NBTItem nbti = new NBTItem(item);
        nbti.setShort("durability", newDurability);
        item = nbti.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        short durability_new = item.getDurability();
        if (durability_new == 0) {
            ItemMeta itemMeta_finish = item.getItemMeta();
            itemMeta_finish.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(itemMeta_finish);
        }
        player.getInventory().setItem(1, item);
        BukkitTask task = new ReloadGunTask(plugin, item, player, this).runTaskLater(plugin, 1);
    }

    public float setNew(float origin) {
        return Math.min(1.0f, Math.max(0.0f, origin));
    }
}
