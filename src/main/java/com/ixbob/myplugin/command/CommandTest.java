
package com.ixbob.myplugin.command;

import com.ixbob.myplugin.entity.ZombieLevel1;
import com.ixbob.myplugin.entity.ZombiePigmanLevel1;
import com.ixbob.myplugin.handler.config.WindowAreaLoader;
import com.ixbob.myplugin.util.Mth;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import static com.ixbob.myplugin.util.Utils.getNearestWindowLoc;

public class CommandTest implements CommandExecutor {

    private final Plugin plugin;

    public CommandTest (Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            WindowAreaLoader.fixAllWindow();

            Location location = player.getLocation();
            location.setPitch(0);

            Location nearestWindowLoc = getNearestWindowLoc(location);
            Vector moveVec3 = calculateMoveVec3(location, nearestWindowLoc);

            location.setYaw((float) Mth.vec3ToYaw(moveVec3));

            ZombieLevel1 zombieLevel1 = new ZombieLevel1(location, plugin);
            zombieLevel1.spawn();
            zombieLevel1.getEntity().setMetadata("moveVec3_x", new FixedMetadataValue(plugin, moveVec3.getX()));
            zombieLevel1.getEntity().setMetadata("moveVec3_z", new FixedMetadataValue(plugin, moveVec3.getZ()));
            zombieLevel1.getEntity().setMetadata("onTheRoadToWindow", new FixedMetadataValue(plugin, true));
            zombieLevel1.getEntity().setMetadata("frontAreaContainWoodStep", new FixedMetadataValue(plugin, false));

            ZombiePigmanLevel1 zombiePigmanLevel1 = new ZombiePigmanLevel1(location, plugin);
            zombiePigmanLevel1.spawn();
            zombiePigmanLevel1.getEntity().setMetadata("moveVec3_x", new FixedMetadataValue(plugin, moveVec3.getX()));
            zombiePigmanLevel1.getEntity().setMetadata("moveVec3_z", new FixedMetadataValue(plugin, moveVec3.getZ()));
            zombiePigmanLevel1.getEntity().setMetadata("onTheRoadToWindow", new FixedMetadataValue(plugin, true));
            zombiePigmanLevel1.getEntity().setMetadata("frontAreaContainWoodStep", new FixedMetadataValue(plugin, false));
//            BukkitTask task = new TestTask(item).runTaskTimer(plugin, 0, 1);
        }
        return true;
    }

    private Vector calculateMoveVec3(Location fromLoc, Location windowLoc) {
        return new Vector(windowLoc.getX() - fromLoc.getX(), 0, windowLoc.getZ() - fromLoc.getZ()).normalize();
    }
}