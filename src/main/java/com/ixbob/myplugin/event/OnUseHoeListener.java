package com.ixbob.myplugin.event;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class OnUseHoeListener implements Listener {
    private final Main plugin;
    private Player eventPlayer;

    public OnUseHoeListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onUseWoodenHoe(PlayerInteractEvent event) {
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
                int ammo_origin;
                String using_gun_type;
                switch (nbtItem.getString("gun_name")) {
                    case ("shou_qiang"): {
                        ammo_origin = player.getMetadata("shou_qiang_ammo").get(0).asInt();
                        using_gun_type = "shou_qiang";
                        break;
                    }
                    case ("bu_qiang"): {
                        ammo_origin = player.getMetadata("bu_qiang_ammo").get(0).asInt();
                        using_gun_type = "bu_qiang";
                        break;
                    }
                    default:
                        throw new NullPointerException("Are you kidding me? no gun matches.");
                }

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
                    bulletMove(armorStand);

                    item.setDurability((short) (item.getDurability() + 1));
                    if (item.getDurability() >= 59) {
                        reloadGunAmmo(item, player);
                    }
                    player.setExp(0f);
                    nbtItem.setFloat("cooldown_progress", 0.0f);
                    item = nbtItem.getItem();
                    player.getInventory().setItemInMainHand(item);
                    float addExpPerCount = calculateAddExpCount(1.0f);
                    shotCoolDown(player, addExpPerCount, item);

                    int ammo_left = ammo_origin - 1;
                    switch (using_gun_type) {
                        case ("shou_qiang"): {
                            player.setMetadata("shou_qiang_ammo", new FixedMetadataValue(plugin, ammo_left));
                            break;
                        }
                        case ("bu_qiang"): {
                            player.setMetadata("bu_qiang_ammo", new FixedMetadataValue(plugin, ammo_left));
                            break;
                        }
                        default:
                            throw new NullPointerException("Are you kidding me? no gun matches.");
                    }
                    player.setLevel(ammo_left);

                    world.playSound(interactLocation, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 2f);


                }
                if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                        && item.getDurability() != 0){
                    item.setDurability((short) 59);
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
        if (inhandItem != null) {
            NBTItem nbtInhandItem = new NBTItem(inhandItem);
            if (Objects.equals(nbtInhandItem.getString("item_type"),"gun")
                    && Objects.equals(player.getInventory().getItemInMainHand(),eventInteractItem)) {
                float newExp = setNew(player.getExp() + addExpPerCount); //Prevent float from reporting errors due to accuracy
                player.setExp(newExp);
            }
        }
        nbtEventItem.setFloat("cooldown_progress", setNew(nbtEventItem.getFloat("cooldown_progress") + addExpPerCount));
        eventInteractItem = nbtEventItem.getItem();
        switch (nbtEventItem.getString("gun_name")) {
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
        switch (nbti.getString("dun_name")) {
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
        BukkitTask task = new ReloadGunTask(item, player, this).runTaskLater(plugin, 1);
    }

    public void bulletMove(ArmorStand armorStand){
        System.out.println("running");
        armorStand.teleport(armorStand.getLocation().add(armorStand.getLocation().getDirection().multiply(1.2)));
        armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, armorStand.getMetadata("fly_distance").get(0).asInt() + 1));
        List<Entity> nearbyEntities = armorStand.getNearbyEntities(0.5,0.5,0.5);
        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity.getType() != EntityType.DROPPED_ITEM
                        && entity.getType() != EntityType.ARROW
                        && entity.getType() != EntityType.LINGERING_POTION
                        && entity.getType() != EntityType.SPLASH_POTION) {
                    LivingEntity nearbyEntity = (LivingEntity) nearbyEntities.get(0);
                    if (nearbyEntity.getType() == EntityType.ZOMBIE
                            || nearbyEntity.getType() == EntityType.SKELETON
                            || nearbyEntity.getType() == EntityType.CREEPER
                            || nearbyEntity.getType() == EntityType.SPIDER) {
                        nearbyEntity.damage(5, eventPlayer);
                        armorStand.remove();
                        return;
                    }
                }
            }
        }
        if (armorStand.getLocation().getBlock().getType() != Material.AIR
                || armorStand.getMetadata("fly_distance").get(0).asInt() >= 20) {
            armorStand.remove();
            return;
        }
        armorStand.getWorld().spawnParticle(Particle.CRIT, armorStand.getLocation(),  1, 0, 0, 0, 0);
        BukkitTask task = new BulletMoveTask(armorStand, this).runTaskLater(plugin, 2);
    }

    public float setNew(float origin) {
        return Math.min(1.0f, Math.max(0.0f, origin));
    }
}
