package com.ixbob.myplugin.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.task.PlayerRespawnCountDowner;
import com.ixbob.myplugin.util.CorpseUtil;
import com.ixbob.myplugin.util.PlayerCorpseTransit;
import com.ixbob.myplugin.util.Utils;
import com.ixbob.myplugin.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static com.ixbob.myplugin.util.Utils.getGround;

public class OnPlayerDeathListener implements Listener {
    private final Plugin plugin;
    private int entityID;
    private Location location;

    public OnPlayerDeathListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double health = player.getHealth();
            if (health <= event.getDamage()) {
                player.setGameMode(GameMode.SPECTATOR);
                event.setCancelled(true);
                player.setHealth(20);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    String playerName = player.getName();
                    this.location = getGround(player.getLocation());

                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    EntityPlayer entityPlayer = craftPlayer.getHandle();

                    MinecraftServer minecraftServer = entityPlayer.server;
                    WorldServer worldServer = entityPlayer.x(); //getWorldServer
                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerName);

                    String DataFromName = Utils.loadJsonAsStringFromUrl("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                    JSONObject JsonFromName = JSON.parseObject(DataFromName);
                    if (JsonFromName != null) {
                        String UUID = JsonFromName.getString("id");
                        String DataFromUUID = Utils.loadJsonAsStringFromUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID + "?unsigned=false");
                        JSONObject JsonFromUUID = JSON.parseObject(DataFromUUID).getJSONArray("properties").getJSONObject(0);

                        String texture = JsonFromUUID.getString("value");
                        String signature = JsonFromUUID.getString("signature");
                        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
                    }

                    EntityPlayer npc = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(minecraftServer.getWorld()));
                    this.entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
                    npc.h(entityID); //h: setID
                    Main.playerCorpseTransit.put(player, npc);

                    Packet<?>[] packets = {new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc),
                            new PacketPlayOutNamedEntitySpawn(npc)};
                    Utils.sendNMSPacketsToAllPlayers(packets);

                    try {
                        sleep();
                        enableOutsideSkin();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Location spawnTextLoc = location.add(0, 1.0, 0);
                        ArmorStand text1Stand = (ArmorStand) Bukkit.getWorlds().get(0).spawnEntity(spawnTextLoc, EntityType.ARMOR_STAND);
                        ArmorStand text2Stand = (ArmorStand) Bukkit.getWorlds().get(0).spawnEntity(spawnTextLoc.add(0, -0.25, 0), EntityType.ARMOR_STAND);
                        initArmorStandText(text1Stand);
                        initArmorStandText(text2Stand);
                        text1Stand.setCustomName(String.format(LangLoader.get("player_help_respawn_time_left_line1"), playerName));
                        text2Stand.setCustomName(String.format(LangLoader.get("player_help_respawn_time_left_line2"), 20.0f));
                        player.setMetadata("needHelpToRespawn", new FixedMetadataValue(plugin, true));
                        player.setMetadata("respawnTimeLeft", new FixedMetadataValue(plugin, 20.0f));
                        player.setMetadata("text1Stand_uuid", new FixedMetadataValue(plugin, text1Stand.getUniqueId().toString())); //绑定属于该玩家的2个显示文字的盔甲架，便于获取
                        player.setMetadata("text2Stand_uuid", new FixedMetadataValue(plugin, text2Stand.getUniqueId().toString()));

                        PlayerRespawnCountDowner respawnCountDowner = new PlayerRespawnCountDowner(player, text1Stand, text2Stand, location);
                        EntityArmorStand text1EntityStand = ((CraftArmorStand)text1Stand).getHandle();
                        EntityArmorStand text2EntityStand = ((CraftArmorStand)text2Stand).getHandle();
                        Packet<?>[] removeStandPackets = {new PacketPlayOutEntityDestroy(text1EntityStand.getId()),
                        new PacketPlayOutEntityDestroy(text2EntityStand.getId())};
                        Utils.sendNMSPackets(removeStandPackets, player);

                        //this method returns a taskID when while scheduling it,
                        int taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, respawnCountDowner, 0, 2);
                        respawnCountDowner.setTaskID(taskID);
                    });
                });
            }
        }
    }
    private void sleep() throws Exception {
        Location bedLocation1 = new Location(location.getWorld(), 1, 1, 1);
        Location bedLocation2 = new Location(location.getWorld(), 2, 1, 1);
        Location bedLocation3 = new Location(location.getWorld(), 3, 1, 1);
        Location bedLocation4 = new Location(location.getWorld(), 4, 1, 1);
        Class<?> packetClass = Utils.getNMSClass("PacketPlayOutBed");
        Object bedPacket = packetClass.getDeclaredConstructor().newInstance();
        CorpseUtil.setValue(bedPacket, "a", entityID);
        Location corpseLocation = getGround(location).add(0, 0.125, 0);
        //尸体血
        double x = corpseLocation.getBlockX();
        double y = corpseLocation.getBlockY();
        double z = corpseLocation.getBlockZ();
        World world = Bukkit.getWorlds().get(0);

        for (int plX = (int) x - 1; plX <= x + 1; plX++) {
            for (int plZ = (int) z - 1; plZ <= z + 1; plZ++) {
                if (world.getBlockAt(plX, (int)y, plZ).getType() == Material.AIR) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int n = (int) Math.floor(Math.random()*2);
                        if (n == 0) {
                            player.sendBlockChange(new Location(world, plX, y, plZ), Material.REDSTONE_WIRE, (byte)0);
                        }
                    }
                };
            }
        }
        //随机躺下朝向
        Location bedLocation = null;
        int posNum = (int) (Math.floor(Math.random() * 4) + 1);
        switch (posNum) {
            case 1: bedLocation = bedLocation1; break;
            case 2: bedLocation = bedLocation2; break;
            case 3: bedLocation = bedLocation3; break;
            case 4: bedLocation = bedLocation4; break;
        }
        assert bedLocation != null;
        CorpseUtil.setValue(bedPacket, "b", Utils.getNMSClass("BlockPosition")
                .getConstructor(int.class, int.class, int.class)
                .newInstance(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte)(posNum-1));
        }

        CorpseUtil.sendPacketObj(bedPacket);
        teleport(corpseLocation);
    }

    private void teleport(Location var1) throws Exception {
        double x = var1.getX();
        double y = var1.getY();
        double z = var1.getZ();
        Class<?> packetClass = Utils.getNMSClass("PacketPlayOutEntityTeleport");
        Object packet = packetClass.getDeclaredConstructor().newInstance();
        CorpseUtil.setValue(packet, "a", entityID);
        CorpseUtil.setValue(packet, "b", x);
        CorpseUtil.setValue(packet, "c", y);
        CorpseUtil.setValue(packet, "d", z);
        CorpseUtil.setValue(packet, "e", (byte)(var1.getYaw()));
        CorpseUtil.setValue(packet, "f", (byte)(var1.getPitch()));
        CorpseUtil.sendPacketObj(packet);
    }

    private void enableOutsideSkin() throws Exception {
        DataWatcher dataWatcher = new DataWatcher(null);
//        https://wiki.vg/Entity_metadata#Player
        DataWatcherObject<Byte> displayedPartsObject = new DataWatcherObject<>(13, DataWatcherRegistry.a);
        byte displayedSkinParts = (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40);
        dataWatcher.register(displayedPartsObject, displayedSkinParts);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityID, dataWatcher, true);
        CorpseUtil.sendPacketObj(packet);
    }

    private void initArmorStandText(ArmorStand armorStand) {
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setMarker(true);
    }
}
