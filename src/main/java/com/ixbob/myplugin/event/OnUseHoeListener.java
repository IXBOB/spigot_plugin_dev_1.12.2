package com.ixbob.myplugin.event;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.task.BulletMoveTask;
import com.ixbob.myplugin.task.ReloadGunAmmoTask;
import com.ixbob.myplugin.task.ShotCoolDownTask;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Objects;

import static com.ixbob.myplugin.GunProperties.*;

public class OnUseHoeListener implements Listener {
    private final Main plugin;
    private Player eventPlayer;
    private String usingGunName;
    private GunType usingGunTypeInstance;
    private Vector interactDirection;
    private Location interactLocation;
    private World world;

    public OnUseHoeListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onUseHoe(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Action action = event.getAction();
        Player player = event.getPlayer();
        this.eventPlayer = player;
        this.world = event.getPlayer().getWorld();

        if (event.getHand() == EquipmentSlot.HAND
                && item != null) {

            NBTItem nbtItem = new NBTItem(item);

            if (Objects.equals(nbtItem.getString("item_type"), "gun")) {

                event.setCancelled(true);
                GunProperties.GunType gunType = GunProperties.GunType.valueOf(nbtItem.getString("gun_name").toUpperCase());
                int ammo_origin = player.getMetadata(gunType.getPlayerAmmoMetadataKey()).get(0).asInt();
                int current_magazine_ammo = player.getMetadata(gunType.getPlayerMagazineAmmoMetadataKey()).get(0).asInt();
                System.out.println(current_magazine_ammo);
//                System.out.println(gunType);
                usingGunName = gunType.getTypeName();
                usingGunTypeInstance = GunProperties.GunType.valueOf(usingGunName.toUpperCase());

                if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                        && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        && nbtItem.getFloat("cooldown_progress") == 1.0f
                        && ammo_origin > 0) {
                    this.interactDirection = player.getLocation().getDirection();
                    this.interactLocation = new Location(world, player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ());

                    if (Objects.equals(usingGunName, "xiandan_qiang")) {
                        Location initLocation = this.interactLocation;
                        Vector initDirection = this.interactDirection;
                        for (int i = 1; i <= 6; i++) {
                            this.interactLocation = initLocation;
                            this.interactDirection = initDirection;
                            this.interactLocation.add(new Location(world, (Math.random()-0.5)*0.15, (Math.random()-0.5)*0.15, (Math.random()-0.5)*0.15));
                            this.interactDirection.add(new Vector((Math.random()-0.5)*0.35, (Math.random()-0.5)*0.35, (Math.random()-0.5)*0.35)).normalize();
                            spawnBullet();
                        }
                    }
                    else {
                        spawnBullet();
                    }

                    int ammo_left = ammo_origin - 1;

                    player.setMetadata(gunType.getPlayerAmmoMetadataKey(),new FixedMetadataValue(plugin, ammo_left));
                    current_magazine_ammo -= 1;
                    player.setMetadata(gunType.getPlayerMagazineAmmoMetadataKey(), new FixedMetadataValue(plugin, current_magazine_ammo));
                    player.setExp(0f);
                    player.setLevel(ammo_left);
//                    System.out.println(current_magazine_ammo);
//                    System.out.println(ammo_left);
                    nbtItem.setFloat("cooldown_progress", 0.0f);
                    item = nbtItem.getItem();
                    player.getInventory().setItemInMainHand(item);
                    float addExpPerCount = calculateAddExpCount(gunCoolDownTime.get(usingGunTypeInstance));
                    world.playSound(interactLocation, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 2f);
                    if (current_magazine_ammo == 0) {
                        item.setDurability(gunDurabilityLegacy.get(usingGunTypeInstance).shortValue());
                        reloadGunAmmo(item, player);
                    }
                    else {
                        item.setAmount(current_magazine_ammo);
                    }
                    ShotCoolDownTask shotCoolDownTask = new ShotCoolDownTask(player, addExpPerCount, item);
                    int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, shotCoolDownTask, 0, 0);
                    shotCoolDownTask.setTaskID(taskID);
                }
                if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                        && item.getAmount() != gunMagazineFullAmmo.get(usingGunTypeInstance)
                        && !nbtItem.getBoolean("reloading")){
                    item.setAmount(1);
                    item.setDurability(gunDurabilityLegacy.get(usingGunTypeInstance).shortValue());
                    reloadGunAmmo(item, player);
                }
            }
        }


    }

    public float calculateAddExpCount (float coolDownTime) {   //Update xp bar per 0.05s (1 tick)

        float addExpCount = coolDownTime / 0.05f;  //fill it with how many times
        return 1.0f / addExpCount;  //fill how much per time
    }

    public void reloadGunAmmo(ItemStack item, Player player) {
        ReloadGunAmmoTask reloadGunAmmoTask = new ReloadGunAmmoTask(item, player, this, gunReloadAmmoTime, gunDurabilityLegacy, gunMagazineFullAmmo, plugin);
        int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, reloadGunAmmoTask, 0, 0);
        reloadGunAmmoTask.setTaskID(taskID);
    }



    public void spawnBullet() {
        Location spawnCacheLocation = new Location(world ,interactLocation.getX(), interactLocation.getY() + 100d, interactLocation.getZ());
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(spawnCacheLocation, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.teleport(interactLocation);
        armorStand.teleport(armorStand.getLocation().setDirection(interactDirection));
        armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, 0));
        armorStand.setMetadata("owner", new FixedMetadataValue(plugin, eventPlayer.getName()));
        armorStand.setMetadata("belong_gun_type", new FixedMetadataValue(plugin, usingGunName));
        BulletMoveTask bulletMoveTask = new BulletMoveTask(armorStand);
        int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, bulletMoveTask, 0, 0);
        bulletMoveTask.setTaskID(taskID);
    }
}
