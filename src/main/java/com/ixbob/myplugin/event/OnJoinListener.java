package com.ixbob.myplugin.event;

import com.ixbob.myplugin.handler.config.LangLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST , ignoreCancelled = true)
    public void onPlayerJoin1(PlayerJoinEvent event) {
        Bukkit.broadcastMessage(LangLoader.get("welcome_message2"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin2(PlayerJoinEvent event) {
        Bukkit.broadcastMessage(LangLoader.get("welcome_message1"));
    }
}
