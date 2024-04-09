package com.ixbob.myplugin.task;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ZombieDestroyTask extends BukkitRunnable {
    private final Plugin plugin;
    public ZombieDestroyTask(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if(entity.getType() == EntityType.ZOMBIE) {
                Zombie zombie = (Zombie) entity;
                Location eyeLocation = zombie.getEyeLocation();
                double posX = eyeLocation.getX();
                double posY = eyeLocation.getY();
                double posZ = eyeLocation.getZ();
                double faceX = eyeLocation.getDirection().getX();
                double faceY = eyeLocation.getDirection().getY();
                double faceZ = eyeLocation.getDirection().getZ();
                double posFrontX = posX + faceX;
                double posFrontY = posY + faceY;
                double posFrontZ = posZ + faceZ; // ��ȡ��ǰ1��Զλ�õķ�������
                World world = Bukkit.getWorlds().get(0);

                List<Block> blockList = new ArrayList<>();
                boolean isListContainsWoodStep = false;
                double loopBlockY = posFrontY + 1;
                for (int i = 1; i <= 3; i++) { // ���������ǰ3x3���򷽿�
                    double loopBlockX = posFrontX + faceZ;
                    double loopBlockZ = posFrontZ - faceX;
                    for(int j = 1; j <= 3; j++) {
                        Block block = world.getBlockAt((int)Math.round(loopBlockX - 0.5), (int)loopBlockY, (int)Math.round(loopBlockZ - 0.5));
                        if (block.getType() == Material.WOOD_STEP) {
                            blockList.add(block);
                            isListContainsWoodStep = true;
                        }
                        loopBlockX -= faceZ;
                        loopBlockZ += faceX;
                    }
                    loopBlockY -= 1;
                }
                if (isListContainsWoodStep) {
                    Block destroyBlock = blockList.get( (int) (Math.random() * blockList.size()) );
                    Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                        Bukkit.getWorlds().get(0).spawnParticle(Particle.BLOCK_CRACK, destroyBlock.getLocation().add(0.5,0,0.5), 12, new MaterialData(Material.WOOD_STEP));
                        destroyBlock.setType(Material.AIR);
                        zombie.setAI(false);
                    });
                }
                else zombie.setAI(true);
            }
        }
    }
}