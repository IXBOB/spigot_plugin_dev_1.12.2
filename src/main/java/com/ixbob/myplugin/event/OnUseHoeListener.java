package com.ixbob.myplugin.event;

import com.ixbob.myplugin.GunProperties;
import com.ixbob.myplugin.Main;
import com.ixbob.myplugin.task.BulletMoveTask;
import com.ixbob.myplugin.task.ReloadGunTask;
import com.ixbob.myplugin.task.ShotCoolDownTask;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ixbob.myplugin.GunProperties.*;

public class OnUseHoeListener implements Listener {
    private final Main plugin;
    private Player eventPlayer;
    private String usingGunName;
    private GunType usingGunTypeInstance;

    public OnUseHoeListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onUseHoe(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Action action = event.getAction();
        Player player = event.getPlayer();
        this.eventPlayer = player;
        World world = event.getPlayer().getWorld();

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
//                int ammo_origin;
//                int shou_qiang_current_magazine_ammo = player.getMetadata("shou_qiang_current_magazine_ammo").get(0).asInt();
//                int bu_qiang_current_magazine_ammo = player.getMetadata("bu_qiang_current_magazine_ammo").get(0).asInt();
//                int current_magazine_ammo;
//                switch (nbtItem.getString("gun_name")) {
//                    case ("shou_qiang"): {
//                        ammo_origin = player.getMetadata("shou_qiang_ammo").get(0).asInt();
//                        current_magazine_ammo = shou_qiang_current_magazine_ammo;
//                        usingGunName = "shou_qiang";
//                        break;
//                    }
//                    case ("bu_qiang"): {
//                        ammo_origin = player.getMetadata("bu_qiang_ammo").get(0).asInt();
//                        current_magazine_ammo = bu_qiang_current_magazine_ammo;
//                        usingGunName = "bu_qiang";
//                        break;
//                    }
//                    default:
//                        throw new NullPointerException("Are you kidding me? no gun matches.");
//                }

                if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                        && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        && nbtItem.getFloat("cooldown_progress") == 1.0f
                        && ammo_origin > 0) {
                    Vector interactDirection = player.getLocation().getDirection();
                    Location interactLocation = new Location(world, player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ());

                    Location spawnCacheLocation = new Location(world,interactLocation.getX(), interactLocation.getY() + 100d, interactLocation.getZ());
                    ArmorStand armorStand = (ArmorStand) world.spawnEntity(spawnCacheLocation, EntityType.ARMOR_STAND);
                    armorStand.setGravity(false);
                    armorStand.setVisible(false);
                    armorStand.teleport(interactLocation);
                    armorStand.teleport(armorStand.getLocation().setDirection(interactDirection));
                    armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, 0));
                    armorStand.setMetadata("owner", new FixedMetadataValue(plugin, player.getName()));
                    armorStand.setMetadata("belong_gun_type", new FixedMetadataValue(plugin, usingGunName));
                    bulletMove(armorStand);

                    int ammo_left = ammo_origin - 1;

                    player.setMetadata(gunType.getPlayerAmmoMetadataKey(),new FixedMetadataValue(plugin, ammo_left));
                    current_magazine_ammo -= 1;
                    player.setMetadata(gunType.getPlayerMagazineAmmoMetadataKey(), new FixedMetadataValue(plugin, current_magazine_ammo));
//                    switch (usingGunName) {
//                        case ("shou_qiang"): {
//                            player.setMetadata("shou_qiang_ammo", new FixedMetadataValue(plugin, ammo_left));
//                            current_magazine_ammo -= 1;
//                            player.setMetadata("shou_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, shou_qiang_current_magazine_ammo));
//                            break;
//                        }
//                        case ("bu_qiang"): {
//                            player.setMetadata("bu_qiang_ammo", new FixedMetadataValue(plugin, ammo_left));
//                            current_magazine_ammo -= 1;
//                            player.setMetadata("bu_qiang_current_magazine_ammo", new FixedMetadataValue(plugin, bu_qiang_current_magazine_ammo));
//                            break;
//                        }
//                        default:
//                            throw new NullPointerException("Are you kidding me? no gun matches.");
//                    }

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
                    shotCoolDown(player, addExpPerCount, item);

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

    public void shotCoolDown(Player player, float addExpPerCount, ItemStack eventInteractItem) {
        NBTItem nbtEventItem = new NBTItem(eventInteractItem);
        ItemStack inhandItem = player.getInventory().getItemInMainHand();
        nbtEventItem.setFloat("cooldown_progress", setNew(nbtEventItem.getFloat("cooldown_progress") + addExpPerCount));
        if (inhandItem != null && inhandItem.getType() != Material.AIR) {
            NBTItem nbtInhandItem = new NBTItem(inhandItem);
            if (Objects.equals(nbtInhandItem.getString("item_type"),"gun")
                    && Objects.equals(player.getInventory().getItemInMainHand(),eventInteractItem)) {
                float newExp = setNew(nbtEventItem.getFloat("cooldown_progress")); //Prevent float from reporting errors due to accuracy
                player.setExp(newExp);
//                System.out.println("newExp: " + newExp);
//                System.out.println("cooldown_progress" + nbtEventItem.getFloat("cooldown_progress"));
            }
        }
        eventInteractItem = nbtEventItem.getItem();
        switch (nbtEventItem.getString("gun_name")) {   // TODO: 等待后期换枪槽位实现完全后优化
            case ("shou_qiang"): {
                player.getInventory().setItem(1, eventInteractItem);
                break;
            }
            case ("bu_qiang"): {
                player.getInventory().setItem(2, eventInteractItem);
                break;
            }
            default:
                throw new NullPointerException("Are you kidding me? no gun matches.");
        }

        BukkitTask task = new ShotCoolDownTask(player, addExpPerCount, eventInteractItem,this).runTaskLater(plugin, 1);
    }

    public void reloadGunAmmo(ItemStack item, Player player) {
        BukkitTask task = new ReloadGunTask(item, player, this, gunReloadAmmoTime, gunDurabilityLegacy, gunMagazineFullAmmo, plugin).runTaskTimer(plugin, 0, 1);
    }

    public void bulletMove(ArmorStand armorStand){
        GunType belongGunType = GunProperties.getGunTypeByString(armorStand.getMetadata("belong_gun_type").get(0).asString());
        float bulletSpeed = gunBulletMoveSpeed.get(belongGunType);
        armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, armorStand.getMetadata("fly_distance").get(0).asFloat() + bulletSpeed));
        List<Entity> nearbyEntities = armorStand.getNearbyEntities(0.1,0.1,0.1);
        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity.getType() != EntityType.DROPPED_ITEM
                        && entity.getType() != EntityType.ARROW
                        && entity.getType() != EntityType.LINGERING_POTION
                        && entity.getType() != EntityType.SPLASH_POTION
                        && entity.getType() != EntityType.EXPERIENCE_ORB) {
                    LivingEntity nearbyEntity = (LivingEntity) nearbyEntities.get(0);
                    if (nearbyEntity.getType() == EntityType.ZOMBIE
                            || nearbyEntity.getType() == EntityType.SKELETON
                            || nearbyEntity.getType() == EntityType.CREEPER
                            || nearbyEntity.getType() == EntityType.SPIDER
                            || nearbyEntity.getType() == EntityType.PIG_ZOMBIE) {
                        nearbyEntity.setMetadata("last_damage_bullet_pos_y", new FixedMetadataValue(plugin, armorStand.getLocation().getY()));
                        eventPlayer.setMetadata("last_damage_using_gun_type", new FixedMetadataValue(plugin, armorStand.getMetadata("belong_gun_type").get(0).asString()));
                        nearbyEntity.damage(gunDamage.get(usingGunTypeInstance), eventPlayer);
                        armorStand.remove();
                        return;
                    }
                }
            }
        }
        if (armorStand.getLocation().getBlock().getType() != Material.AIR
                || armorStand.getMetadata("fly_distance").get(0).asFloat() >= gunBulletMoveDistance.get(belongGunType)) {
            armorStand.remove();
            return;
        }
        armorStand.getWorld().spawnParticle(Particle.CRIT, armorStand.getLocation(),  1, 0, 0, 0, 0);
        armorStand.teleport(armorStand.getLocation().add(armorStand.getLocation().getDirection().multiply(bulletSpeed)));
        BukkitTask task = new BulletMoveTask(armorStand, this).runTaskLater(plugin, 1);
    }

    public float setNew(float origin) {
        return Math.min(1.0f, Math.max(0.0f, origin));
    }
}
