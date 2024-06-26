package com.ixbob.myplugin.command;

import com.ixbob.myplugin.MongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandZombies implements CommandExecutor {
    private String[] inputSubCommand;
    private int executedSubCommandLength;
    private int inputSubCommandLength;
    private Player player;
    private MongoDB mongoDB = new MongoDB("windowLoc");
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
            case ("read"): settings_window_read_(); break;
        }
    }

    private void settings_window_set() {
            String DBCollectionName = "windowLoc";
            MongoDB mongoDB = new MongoDB(DBCollectionName);
            Location location = player.getLocation();
            long id = mongoDB.getCollectionSize() + 1;  //从1开始赋予id值
            DBObject locObj = new BasicDBObject("id", id);
            double x = player.getLocation().getX();
            double y = player.getLocation().getY() + 1.7;  //僵尸眼睛位置
            double z = player.getLocation().getZ();
            locObj.put("x", x);
            locObj.put("y", y);
            locObj.put("z", z);
            mongoDB.insert(locObj);
            player.sendMessage("window set! id: " + id + ", x: " + x + ", y: " + y + ", z: " + z);
    }

    private void settings_window_read_() {
        if (executedSubCommandLength == inputSubCommandLength) {
            return;
        }
        executedSubCommandLength++;
        if (Objects.equals(inputSubCommand[3], "all")) {
            settings_window_read_all();
        } else {
            try {
                settings_window_read_id(Integer.parseInt(inputSubCommand[3]));
            } catch (Exception e) {
                player.sendMessage("§cError parameter. [String] all/[int] <id>");
            }
        }
    }

    private void settings_window_read_all() {
        for (int i = 1; i <= mongoDB.getCollectionSize(); i++) {
            settings_window_read_id(i);
        }
    }

    private void settings_window_read_id(int id) {
        Location readPos = mongoDB.readPos(id);
        double x = readPos.getX();
        double y = readPos.getY();
        double z = readPos.getZ();
        player.sendMessage("window id: " + id + ", x: " + x + ", y: " + y + ", z: " + z);
        Bukkit.getWorlds().get(0).spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, 1);
    }
}
