package com.ixbob.myplugin.task;

import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.util.Utils;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import com.ixbob.myplugin.Main;

public class PlayerSneakToHelpCountDowner implements Runnable{
    private int taskID;
    private float helpTime = 1.50f;
    private final Player doHelpPlayer;
    private final Player getHelpedPlayer;
    private final String doHelpPlayerName;
    private final String getHelpedPlayerName;
    public PlayerSneakToHelpCountDowner(Player doHelpPlayer, Player getHelpedPlayer) {
        this.doHelpPlayer = doHelpPlayer;
        this.getHelpedPlayer = getHelpedPlayer;
        this.doHelpPlayerName = doHelpPlayer.getName();
        this.getHelpedPlayerName = getHelpedPlayer.getName();
    }
    @Override
    public void run() { //repeat every 1 tick.
        getHelpedPlayer.setMetadata("isBeingHelped", new FixedMetadataValue(Main.getInstance(), true));
        helpTime -= 0.05f;
        ArmorStand text1Stand = (ArmorStand) Bukkit.getEntity(java.util.UUID.fromString(getHelpedPlayer.getMetadata("text1Stand_uuid").get(0).asString()));
        ArmorStand text2Stand = (ArmorStand) Bukkit.getEntity(java.util.UUID.fromString(getHelpedPlayer.getMetadata("text2Stand_uuid").get(0).asString()));
        text1Stand.setCustomName(LangLoader.get("player_help_respawning_line1"));
        text2Stand.setCustomName(String.format(LangLoader.get("player_help_respawning_line2"), String.format("%.1f", helpTime)));
        if (!doHelpPlayer.isSneaking()) {
            getHelpedPlayer.setMetadata("isBeingHelped", new FixedMetadataValue(Main.getInstance(), false));
            //仅设置text1Stand，因为text2Stand会在isBeingHelp为false时自动更新文字，在PlayerRespawnCountDowner.java
            text1Stand.setCustomName(String.format(LangLoader.get("player_help_respawn_time_left_line1"), getHelpedPlayerName));
            cancel();
        }
        if (helpTime <= 0.0f) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(String.format(LangLoader.get("player_respawn_success_chat_text"), doHelpPlayerName, getHelpedPlayerName));
            }
            text1Stand.remove();
            text2Stand.remove();
            getHelpedPlayer.setMetadata("isBeingHelped", new FixedMetadataValue(Main.getInstance(), false));
            getHelpedPlayer.setMetadata("needHelpToRespawn", new FixedMetadataValue(Main.getInstance(), false));
            getHelpedPlayer.setMetadata("justRespawned", new FixedMetadataValue(Main.getInstance(), true)); //在PlayerRespawnCountDowner.java中处理，会取消RespawnCountDowner
            getHelpedPlayer.setGameMode(GameMode.ADVENTURE);
            EntityPlayer playerCorpse = Main.playerCorpseTransit.get(getHelpedPlayer);
            Main.playerCorpseTransit.remove(getHelpedPlayer);
            Packet<?> packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, playerCorpse);
            Utils.sendNMSPacketToAllPlayers(packet);
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
