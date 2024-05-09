package com.ixbob.myplugin.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.stream.Stream;

public class GameMainThread implements Runnable {
    private int round;
    private int monsterAllInRound; //当前回合总怪物数
    private long monsterLeft;

    @Override
    public void run() {
        monsterLeft = Bukkit.getWorlds().get(0).getEntities().stream().filter(
                monster -> (monster instanceof LivingEntity
                        && monster.getMetadata("custom_monster").get(0).asBoolean())).count();
    }
    public void init() {
        this.round = 1;
        this.monsterAllInRound = 10;
    }

    public void nextRound() {

    }

    public int getMonsterAllInRound() {
        return monsterAllInRound;
    }

    public void setMonsterAllInRound(int monsterAllInRound) {
        this.monsterAllInRound = monsterAllInRound;
    }

    public long getMonsterLeft() {
        return monsterLeft;
    }

    public void setMonsterLeft(int monsterLeft) {
        this.monsterLeft = monsterLeft;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

}
