package com.ixbob.myplugin;

import com.ixbob.myplugin.command.CommandKit;
import com.ixbob.myplugin.command.CommandTestkit;
import com.ixbob.myplugin.event.OnBreakBlockListener;
import com.ixbob.myplugin.event.OnItemHoldChangeListener;
import com.ixbob.myplugin.event.OnJoinListener;
import com.ixbob.myplugin.event.OnUseHoeListener;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;

public class Main extends JavaPlugin {
    private DBCollection players;
    private DB mcserverdb;
    private MongoClient client;
    @Override
    public void onEnable() {
        this.getCommand("lobby").setExecutor(new CommandKit());
        this.getCommand("testkit").setExecutor(new CommandTestkit(this));

        Listener OnJoinListener = new OnJoinListener();
        getServer().getPluginManager().registerEvents(OnJoinListener, this);

        Listener OnUseHoeListener = new OnUseHoeListener(this);
        getServer().getPluginManager().registerEvents(OnUseHoeListener, this);

        Listener OnItemHoldChangeListener = new OnItemHoldChangeListener();
        getServer().getPluginManager().registerEvents(OnItemHoldChangeListener, this);

        Listener OnBreakBlockListener = new OnBreakBlockListener();
        getServer().getPluginManager().registerEvents(OnBreakBlockListener, this);

        connect("127.0.0.1", 27017);

//        CustomEvent exampleEvent = new CustomEvent("iiixbob");
//        Bukkit.getPluginManager().callEvent(exampleEvent);
//        Bukkit.getPlayer("IXBOB").sendMessage(exampleEvent.getPlayerName());



//        HandlerList.unregisterAll(OnJoinListener);
    }

    public boolean connect(String ip, int port) {
        try {
            client = new MongoClient(ip, port);
            System.out.println("database connected!");
        } catch (UnknownHostException e) {
            System.out.println("Could not connect to database!");
            e.printStackTrace();
            return false;
        }
        mcserverdb = client.getDB("mcserver");
        players = mcserverdb.getCollection("players");
        return true;
    }
}
