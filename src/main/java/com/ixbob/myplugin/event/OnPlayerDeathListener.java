package com.ixbob.myplugin.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class OnPlayerDeathListener implements Listener {
    private PlayerConnection playerConnection;
    private int entityID;
    private Location location;
    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double health = player.getHealth();
            if (health <= event.getDamage()) {
                event.setCancelled(true);
                player.setHealth(20);
                CraftPlayer craftPlayer = (CraftPlayer) player;
                EntityPlayer entityPlayer = craftPlayer.getHandle();

                MinecraftServer minecraftServer = entityPlayer.server;
                WorldServer worldServer = entityPlayer.x(); //getWorldServer
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "BOB");

                EntityPlayer npc = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(minecraftServer.getWorld()));
                this.entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
                npc.h(entityID); //h: setID

                this.playerConnection = entityPlayer.playerConnection;

                //PlayerInfoPacket
                playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                //SpawnPacket
                playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));

                this.location = player.getLocation();
                try {
                    sleep();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void sleep() throws Exception {
        Location bedLocation = new Location(location.getWorld(), 1, 1, 1);
        Class<?> packetClass = getNMSClass("PacketPlayOutBed");
        Object bedPacket = packetClass.getDeclaredConstructor().newInstance();
        setValue(bedPacket, "a", entityID);
        setValue(bedPacket, "b", getNMSClass("BlockPosition")
                .getConstructor(int.class, int.class, int.class)
                .newInstance(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte)0);
        }
        sendPacket(bedPacket);
        teleport(getGround(location).add(0, 0.125, 0));
    }

    private Location getGround (Location var1) {
        return new Location(var1.getWorld(),
                var1.getX(),
                var1.getWorld().getHighestBlockYAt(var1.getBlockX(), var1.getBlockZ()),
                var1.getZ());
    }

    private void teleport(Location var1) throws Exception {
        double x = var1.getX();
        double y = var1.getY();
        double z = var1.getZ();
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityTeleport");
        Object packet = packetClass.getDeclaredConstructor().newInstance();
        setValue(packet, "a", entityID);
        setValue(packet, "b", var1.getX());
        setValue(packet, "c", var1.getY());
        setValue(packet, "d", var1.getZ());
        setValue(packet, "e", (byte)(var1.getYaw()));
        setValue(packet, "f", (byte)(var1.getPitch()));
        sendPacket(packet);
    }

    public Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server.v1_12_R1." + clazz);
    }

    private void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getConnection(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        return conField.get(nmsPlayer);
    }

    private void sendPacket(Object packet) throws Exception{
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket.invoke(getConnection(player), packet);
        }
    }
}
