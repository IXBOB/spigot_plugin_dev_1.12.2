package com.ixbob.myplugin.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnOpenRaffleChestListener implements Listener {
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    @EventHandler
    public void onOpenRaffleChest(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        World world = player.getWorld();
        Block clickedBlock = event.getClickedBlock();

        if( action == Action.RIGHT_CLICK_BLOCK
                && clickedBlock.getType() == Material.CHEST) {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_ACTION);
            BlockPosition blockPosition1 = new BlockPosition(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
            packet.getBlockPositionModifier().write(0, blockPosition1);
            packet.getIntegers().write(0, 1); // index 0: write open chest action
            packet.getIntegers().write(1, 1); // index 1: the id of chest
            sendPacketToAllPlayers(packet);
            int x = clickedBlock.getX();
            int y = clickedBlock.getY();
            int z = clickedBlock.getZ();
            for (int dx = x - 1; dx <= x + 1; dx++) {     // find nearby chest if it is big chest
                for (int dz = z - 1; dz <= z + 1; dz++) {
                    if ((dx == x && dz == z) || Math.abs(dx-x) + Math.abs(dz-z) == 2) {
                        continue;
                    }
                    if (world.getBlockAt(dx, y, dz).getType() == Material.CHEST) {
                        BlockPosition blockPosition2 = new BlockPosition(dx, y, dz);
                        packet.getBlockPositionModifier().write(0, blockPosition2);
                        sendPacketToAllPlayers(packet);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public void sendPacketToAllPlayers(PacketContainer packet) {
        for (Player playerOnline : Bukkit.getOnlinePlayers()) {
            try {
                protocolManager.sendServerPacket(playerOnline, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
