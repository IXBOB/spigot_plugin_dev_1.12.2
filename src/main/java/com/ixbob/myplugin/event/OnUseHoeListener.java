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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
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

        if (event.getHand() ==
                EquipmentSlot.HAND
                && item != null
                && item.getType() == Material.WOOD_HOE) {

            int shou_qiang_ammo_origin = player.getMetadata("shou_qiang_ammo").get(0).asInt();
            NBTItem nbtItem = new NBTItem(item);

            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    && nbtItem.getFloat("cooldown_progress") == 1.0f
                    && shou_qiang_ammo_origin > 0) {
                Vector interactDirection = player.getLocation().getDirection();
                Location interactLocation = new Location(world, player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ());

                Location spawnCacheLocation = new Location(world,interactLocation.getX(), interactLocation.getY() + 100d, interactLocation.getZ());
                ArmorStand armorStand = (ArmorStand) world.spawnEntity(spawnCacheLocation, EntityType.ARMOR_STAND);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.teleport(interactLocation);
                armorStand.teleport(armorStand.getLocation().setDirection(interactDirection));
                armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, (int) 0));
                bulletMove(armorStand);

                player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                item.setDurability((short) (item.getDurability() + 1));
                if (item.getDurability() >= 59) {
                    reloadGunAmmo(plugin, item, player);
                }
                player.setExp(0f);
                nbtItem.setFloat("cooldown_progress", 0.0f);
                item = nbtItem.getItem();
                player.getInventory().setItemInMainHand(item);
                float addExpPerCount = calculateAddExpCount(1.0f);
                shotCoolDown(player, addExpPerCount, item);

                int shou_qiang_ammo_left = shou_qiang_ammo_origin - 1;
                player.setMetadata("shou_qiang_ammo", new FixedMetadataValue(plugin, shou_qiang_ammo_left));
                player.setLevel(shou_qiang_ammo_left);

                world.playSound(interactLocation, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 2f);


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

    public void reloadGunAmmo(JavaPlugin plugin, ItemStack item, Player player) {  //TODO: remove the 'JavaPlugin plugin' here.
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

    public void bulletMove(ArmorStand armorStand){
        System.out.println("running");
        armorStand.teleport(armorStand.getLocation().add(armorStand.getLocation().getDirection().multiply(1.2)));
        armorStand.setMetadata("fly_distance", new FixedMetadataValue(plugin, armorStand.getMetadata("fly_distance").get(0).asInt() + 1));
        List<Entity> nearbyEntities = armorStand.getNearbyEntities(0.5,0.5,0.5);
        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity.getType() != EntityType.DROPPED_ITEM) {
                    LivingEntity nearbyEntity = (LivingEntity) nearbyEntities.get(0);
                    if (nearbyEntity.getType() == EntityType.ZOMBIE
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
