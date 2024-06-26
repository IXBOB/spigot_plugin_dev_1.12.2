package com.ixbob.myplugin;

import com.ixbob.myplugin.command.*;
import com.ixbob.myplugin.event.*;
import com.ixbob.myplugin.handler.config.LangLoader;
import com.ixbob.myplugin.handler.config.MonsterReceiveAmmoHandler;
import com.ixbob.myplugin.handler.config.WindowAreaLoader;
import com.ixbob.myplugin.task.ZombieDestroyTask;
import com.ixbob.myplugin.task.ZombieMoveTask;
import com.ixbob.myplugin.util.PlayerCorpseTransit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
    public static Plugin plugin;
    public static PlayerCorpseTransit playerCorpseTransit = new PlayerCorpseTransit();
    @Override
    public void onEnable() {
        plugin = this;

        MongoDB mongoDB = new MongoDB();
        mongoDB.connect("127.0.0.1", 27017, this);

        LangLoader.init(this);
        WindowAreaLoader.init(this);

        this.getCommand("testkit").setExecutor(new CommandTestkit(this));
        this.getCommand("init").setExecutor(new CommandInit(this));
        this.getCommand("test").setExecutor(new CommandTest(this));
        this.getCommand("zombies").setExecutor(new CommandZombies());

        Listener onJoinListener = new OnJoinListener();
        getServer().getPluginManager().registerEvents(onJoinListener, this);

        Listener onUseHoeListener = new OnUseHoeListener(this);
        getServer().getPluginManager().registerEvents(onUseHoeListener, this);

        Listener onItemHeldChangeListener = new OnItemHeldChangeListener();
        getServer().getPluginManager().registerEvents(onItemHeldChangeListener, this);

//        Listener onBreakBlockListener = new OnBreakBlockListener();
//        getServer().getPluginManager().registerEvents(onBreakBlockListener, this);

        Listener onDamageMonsterListener = new OnDamageMonsterListener(this);
        getServer().getPluginManager().registerEvents(onDamageMonsterListener, this);

        Listener onKillMonsterListener = new OnKillMonsterListener();
        getServer().getPluginManager().registerEvents(onKillMonsterListener, this);

        Listener onOpenRaffleChestListener = new OnOpenRaffleChestListener();
        getServer().getPluginManager().registerEvents(onOpenRaffleChestListener, this);

//        Listener onInventoryClickListener = new OnInventoryClickListener();
//        getServer().getPluginManager().registerEvents(onInventoryClickListener, this);

        Listener onPlayerDeathListener = new OnPlayerDeathListener(this);
        getServer().getPluginManager().registerEvents(onPlayerDeathListener, this);

        Listener onPlayerDropItemListener = new OnPlayerDropItemListener();
        getServer().getPluginManager().registerEvents(onPlayerDropItemListener, this);

        Listener playerSneakingListener = new PlayerSneakingListener();
        getServer().getPluginManager().registerEvents(playerSneakingListener, this);

        BukkitTask zombieMoveTask = new ZombieMoveTask(this).runTaskTimerAsynchronously(this, 0, 1);
        BukkitTask zombieDestroyTask = new ZombieDestroyTask(this).runTaskTimerAsynchronously(this, 0, 20);

//        CustomEvent exampleEvent = new CustomEvent("iiixbob");
//        Bukkit.getPluginManager().callEvent(exampleEvent);
//        Bukkit.getPlayer("IXBOB").sendMessage(exampleEvent.getPlayerName());


//        HandlerList.unregisterAll(OnJoinListener);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MonsterReceiveAmmoHandler(), 0, 0);
    }

    public static Plugin getInstance() {
        return plugin;
    }

}
