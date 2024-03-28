package com.ixbob.myplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class OnJoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST , ignoreCancelled = true)
    public void onPlayerJoin1(PlayerJoinEvent event) {
        Bukkit.broadcastMessage("Welcome to the server2");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin2(PlayerJoinEvent event) {
        Bukkit.broadcastMessage("Welcome to the server1");
    }
}
