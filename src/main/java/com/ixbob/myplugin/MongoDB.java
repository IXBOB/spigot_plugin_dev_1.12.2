package com.ixbob.myplugin;

import com.mongodb.*;
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
    public long getCollectionSize(String collectionName) {
        return mcserverdb.getCollection(collectionName).getCount();
    }
    public String getCollectionName() {
        return collection.getName();
    }
    public void insert(DBObject object) {
        collection.insert(object);
    }
    public void insertTest() {
        DBObject obj = new BasicDBObject("test_key", "123456");
        obj.put("test_key2", "555555");
        collection.insert(obj);
    }
}
