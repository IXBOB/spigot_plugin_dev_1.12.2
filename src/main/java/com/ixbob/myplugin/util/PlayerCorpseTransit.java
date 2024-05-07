package com.ixbob.myplugin.util;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCorpseTransit {
    private Map<Player, EntityPlayer> map = new HashMap<Player, EntityPlayer>();

    public PlayerCorpseTransit() {
    }

    public void put(Player player, EntityPlayer corpse) {
        map.put(player, corpse);
    }

    public void remove(Player player) {
        map.remove(player);
    }

    public EntityPlayer get(Player player) {
        return map.get(player);
    }
}
