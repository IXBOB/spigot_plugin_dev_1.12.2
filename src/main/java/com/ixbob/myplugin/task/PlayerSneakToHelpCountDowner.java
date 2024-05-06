package com.ixbob.myplugin.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerSneakToHelpCountDowner implements Runnable{
    private int taskID = 12389;
    private final Player player;
    public PlayerSneakToHelpCountDowner(Player player) {
        this.player = player;
    }
    @Override
    public void run() { //repeat every 1 tick. ATTENTION: 1
        System.out.println(Math.random());
        if (!player.isSneaking()) {
            cancel();
        }
    }

    public void setTaskID(int id) {
        this.taskID = id;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
