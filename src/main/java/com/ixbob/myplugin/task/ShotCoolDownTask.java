package com.ixbob.myplugin.task;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ShotCoolDownTask implements Runnable {
    private final Player player;
    private final float addExpPerCount;
    private ItemStack eventInteractItem;
    private int taskID;

    public ShotCoolDownTask(Player player, float addExpPerCount,ItemStack eventInteractItem) {
        this.player = player;
        this.addExpPerCount = addExpPerCount;
        this.eventInteractItem = eventInteractItem;
    }

    @Override
    public void run() {
        NBTItem nbtEventInteractItem = new NBTItem(eventInteractItem);
        if (nbtEventInteractItem.getFloat("cooldown_progress") < 1.0f){
            NBTItem nbtEventItem = new NBTItem(eventInteractItem);
            ItemStack inHandItem = player.getInventory().getItemInMainHand();
            nbtEventItem.setFloat("cooldown_progress", setNewExp(nbtEventItem.getFloat("cooldown_progress") + addExpPerCount));
            if (inHandItem != null && inHandItem.getType() != Material.AIR) {
                NBTItem nbtInHandItem = new NBTItem(inHandItem);
                if (Objects.equals(nbtInHandItem.getString("item_type"),"gun")
                        && Objects.equals(player.getInventory().getItemInMainHand(),eventInteractItem)) {
                    float newExp = setNewExp(nbtEventItem.getFloat("cooldown_progress")); //Prevent float from reporting errors due to accuracy
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
                case ("xiandan_qiang"): {
                    player.getInventory().setItem(3, eventInteractItem);
                    break;
                }
                case ("dianyong_qiang"): {
                    player.getInventory().setItem(4, eventInteractItem);
                    break;
                }case ("guci"): {
                    player.getInventory().setItem(5, eventInteractItem);
                    break;
                }
                default:
                    throw new NullPointerException("Are you kidding me? no gun matches.");
            }
        }
        else {
            cancel();
        }
    }

    public float setNewExp(float origin) { //Prevent float from reporting errors due to accuracy
        return Math.min(1.0f, Math.max(0.0f, origin));
    }

    public void setTaskID(int id) {
        this.taskID = id;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
