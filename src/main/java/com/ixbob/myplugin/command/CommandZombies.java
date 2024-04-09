package com.ixbob.myplugin.command;

import com.ixbob.myplugin.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandZombies implements CommandExecutor {
    private String[] inputSubCommand;
    private int executedSubCommandLength;
    private int inputSubCommandLength;
    private Player player;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            player = (Player) commandSender;
            inputSubCommandLength = strings.length;
            if (inputSubCommandLength == 0) {
                player.sendMessage("Parameters needed.");
            }
            if (inputSubCommandLength >= 1) {
                inputSubCommand = strings;
                executedSubCommandLength = 1;
                switch (inputSubCommand[0]) {
                    case ("settings"): settings_(); break;
                }
            }
        }
        return true;
    }
    private void settings_() {
        if (executedSubCommandLength == inputSubCommandLength) {
            return;
        }
        executedSubCommandLength++;
        switch (inputSubCommand[1]) {
            case ("window"): settings_window_(); break;
        }
    }
    private void settings_window_() {
        if (executedSubCommandLength == inputSubCommandLength) {
            return;
        }
        executedSubCommandLength++;
        switch (inputSubCommand[2]) {
            case ("set"): settings_window_set(); break;
        }
    }
    private void settings_window_set() {
            String DBCollectionName = "windowLoc";
            MongoDB mongoDB = new MongoDB(DBCollectionName);
            Location location = player.getLocation();
            long id = mongoDB.getCollectionSize(DBCollectionName);
            System.out.println(id);
            DBObject locObj = new BasicDBObject("id", id);
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();
            locObj.put("x", x);
            locObj.put("y", y);
            locObj.put("z", z);
            mongoDB.insert(locObj);
            player.sendMessage("window set! id: " + id + ", x: " + x + ", y: " + y + ", z: " + z);
    }
}
