package com.ixbob.myplugin.command;

import com.ixbob.myplugin.MongoDB;
import com.mongodb.Mongo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandSetWindow implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            MongoDB mongoDB = new MongoDB("windowPos"); // not finished
        }
        return true;
    }
}
