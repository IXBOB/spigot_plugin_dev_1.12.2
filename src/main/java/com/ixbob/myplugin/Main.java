package com.ixbob.myplugin;

import com.ixbob.myplugin.command.*;
import com.ixbob.myplugin.event.*;
import com.ixbob.myplugin.task.ZombieDestroyTask;
import com.ixbob.myplugin.task.ZombieMoveTask;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {

        MongoDB mongoDB = new MongoDB();
        mongoDB.connect("127.0.0.1", 27017, this);

        LangLoader.init(this);

        this.getCommand("lobby").setExecutor(new CommandLobby());
        this.getCommand("testkit").setExecutor(new CommandTestkit(this));
        this.getCommand("init").setExecutor(new CommandInit(this));
        this.getCommand("test").setExecutor(new CommandTest(this));
        this.getCommand("zombies").setExecutor(new CommandZombies());

        Listener OnJoinListener = new OnJoinListener();
        getServer().getPluginManager().registerEvents(OnJoinListener, this);

        Listener OnUseHoeListener = new OnUseHoeListener(this);
        getServer().getPluginManager().registerEvents(OnUseHoeListener, this);

        Listener OnItemHoldChangeListener = new OnItemHoldChangeListener();
        getServer().getPluginManager().registerEvents(OnItemHoldChangeListener, this);

//        Listener OnBreakBlockListener = new OnBreakBlockListener();
//        getServer().getPluginManager().registerEvents(OnBreakBlockListener, this);

        Listener OnDamageMonsterListener = new OnDamageMonsterListener(this);
        getServer().getPluginManager().registerEvents(OnDamageMonsterListener, this);

        Listener OnKillMonsterListener = new OnKillMonsterListener();
        getServer().getPluginManager().registerEvents(OnKillMonsterListener, this);

        Listener OnOpenRaffleChestListener = new OnOpenRaffleChestListener();
        getServer().getPluginManager().registerEvents(OnOpenRaffleChestListener, this);

        BukkitTask zombieMoveTask = new ZombieMoveTask(this).runTaskTimerAsynchronously(this, 0, 1);
        BukkitTask zombieDestroyTask = new ZombieDestroyTask(this).runTaskTimerAsynchronously(this, 0, 20);




//        CustomEvent exampleEvent = new CustomEvent("iiixbob");
//        Bukkit.getPluginManager().callEvent(exampleEvent);
//        Bukkit.getPlayer("IXBOB").sendMessage(exampleEvent.getPlayerName());


//        HandlerList.unregisterAll(OnJoinListener);
    }
}
