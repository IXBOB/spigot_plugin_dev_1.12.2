package com.ixbob.myplugin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class CommandInit implements CommandExecutor {
    private final Plugin plugin;
    public CommandInit (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Scoreboard scoreboard = commandSender.getServer().getScoreboardManager().getNewScoreboard();
        Objective scoreboardObjective = scoreboard.registerNewObjective("main", "dummy");
        scoreboardObjective.setDisplayName(ChatColor.YELLOW + "��ʬĩ��");
        Player player = (Player) commandSender;
        player.setMetadata("coin_count", new FixedMetadataValue(plugin, 0));
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboardObjective.getScore(player.getDisplayName() + " " + ChatColor.GOLD + 0).setScore(0);
        for (Player playerOnline : Bukkit.getOnlinePlayers()) {
            playerOnline.setScoreboard(scoreboard);
        }
        player.setScoreboard(scoreboard);
        return true;
    }
}
