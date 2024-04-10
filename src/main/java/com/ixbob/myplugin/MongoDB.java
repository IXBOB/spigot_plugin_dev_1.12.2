package com.ixbob.myplugin;

import com.mongodb.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.logging.Level;

public class MongoDB {
    private DBCollection collection;
    private static DB mcserverdb;
    private static MongoClient client;
    public MongoDB(String collectionName) {
        collection = mcserverdb.getCollection(collectionName);
    }
    public MongoDB() {}
    public void setCollection(String collectionName) {
        collection = mcserverdb.getCollection(collectionName);
    }
    public void connect(String ip, int port, Plugin plugin) {
        try {
            client = new MongoClient(ip, port);
            plugin.getLogger().log(Level.INFO, "database connected!");
            mcserverdb = client.getDB("mcserver");
        } catch (UnknownHostException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to database!", e);
        }
    }
    public long getCollectionSize() {
        return collection.getCount();
    }
    public String getCollectionName() {
        return collection.getName();
    }
    public void insert(DBObject object) {
        collection.insert(object);
    }
    public double readPos(int id, String type) {
        DBObject found = findById(id);
        return (double) found.get(type);
    }
    public Location readPos(int id) {
        DBObject found = findById(id);
        double x = (double) found.get("x");
        double y = (double) found.get("y");
        double z = (double) found.get("z");
        return new Location(Bukkit.getWorlds().get(0), x, y, z);
    }

//    public void insertTest() {
//        DBObject obj = new BasicDBObject("test_key", "123456");
//        obj.put("test_key2", "555555");
//        collection.insert(obj);
//    }

    private DBObject findById (int id) {
        DBObject r = new BasicDBObject("id", id);
        DBObject found = collection.findOne(r);
        if (found == null) {
            Bukkit.getLogger().log(Level.SEVERE, "No pos found. Have you set the window position?");
            throw new NullPointerException();
        }
        return found;
    }
}
