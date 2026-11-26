package com.虫巢计划.model;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * 感官编码器 - 主线程每tick调用，采集世界状态
 */
public class SensoryFrameEncoder {
    
    /**
     * 从世界状态编码感官帧
     */
    public static SensoryFrame encode(ServerWorld world, Entity entity) {
        SensoryFrame frame = new SensoryFrame();
        frame.frameTick = (int) world.getTime();
        frame.worldSeed = world.getSeed();
        frame.dimensionId = world.getDimension().equals(net.minecraft.world.dimension overloads) ? 0 : 0;
        frame.isDark = !world.isDay();
        frame.daylight = world.getTimeOfDay();
        
        if (entity != null) {
            Vec3d pos = entity.getPos();
            frame.entityX = (float) pos.x;
            frame.entityY = (float) pos.y;
            frame.entityZ = (float) pos.z;
            frame.healthPercent = entity.getMaxHealth() > 0 ? 
                (int) (entity.getHealth() / entity.getMaxHealth() * 100) : 0;
        }
        
        // TODO: 添加更多感官维度
        // - 附近玩家距离
        // - 附近实体类型
        // - 光照等级
        // - 方块类型
        
        return frame;
    }
    
    /**
     * 简化的生物群落采样
     */
    public static float[] sampleBiomes(ServerWorld world, Entity entity) {
        if (entity == null) return new float[4];
        int x = (int) entity.getX();
        int z = (int) entity.getZ();
        float[] biomes = new float[4];
        for (int i = 0; i < 4; i++) {
            biomes[i] = world.getBiome(new net.minecraft.util.math.BlockPos(x + i, entity.getY(), z)).hashCode() % 100 / 100.0f;
        }
        return biomes;
    }
}
