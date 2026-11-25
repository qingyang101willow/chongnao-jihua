package com.虫巢计划;

import com.虫巢计划.model.SensoryFrame;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Собирает текущий sensory frame из мира (только серверные tick-и).
 * Вызывается из Main tick loop (по событию или напрямую из mod initializer).
 */
public class WorldSensor {

    public static final Logger LOGGER = LoggerFactory.getLogger(WorldSensor.class);

    private static volatile SensoryFrame currentFrame;
    private static volatile long lastCollectTick = -1;

    public static void collect(ServerWorld world, PlayerEntity player, MotherBugEntity bug) {
        if (world.getTime() == lastCollectTick && currentFrame != null) return;
        lastCollectTick = world.getTime();

        SensoryFrame f = new SensoryFrame();
        f.frameTick = (int) world.getTime();
        f.worldSeed = world.getSeed();
        // dimensionId в 1.21+ — getRegistryKey().getValue().getY() не нужен, используем getId через registry
        f.dimensionId = world.getDimension().getId();
        f.isDark = !world.isDay();
        f.daylight = world.getTimeOfDay();

        if (player != null) {
            Vec3d p = player.getPos();
            f.playerX = (float) p.x; f.playerY = (float) p.y; f.playerZ = (float) p.z;
        }
        if (bug != null) {
            Vec3d e = bug.getPos();
            f.entityX = (float) e.x; f.entityY = (float) e.y; f.entityZ = (float) e.z;
            f.healthPercent = bug.getMaxHealth() > 0 ? Math.round(bug.getHealth() / bug.getMaxHealth() * 100) : 0;
        }
        currentFrame = f;
        LOGGER.debug("Frame t={} dim={} dark={}", f.frameTick, f.dimensionId, f.isDark);
    }

    public static SensoryFrame peek() {
        return currentFrame;
    }
}