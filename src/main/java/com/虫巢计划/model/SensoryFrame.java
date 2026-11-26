package com.虫巢计划.model;

/**
 * 感官帧 - 从世界采集的只读快照
 * 
 * 线程安全约束：
 * - 此对象在主线程创建，传递给脑线程后变为只读
 * - 脑线程禁止修改任何字段
 * - 建议后续改为 record 或所有字段 final
 */
public final class SensoryFrame {

    public enum Presence { NONE, UNKNOWN, EMPTY, OCCUPIED }

    public final long worldSeed;
    public final int frameTick;
    public final float playerX, playerY, playerZ;
    public final float entityX, entityY, entityZ;
    public final int dimensionId;
    public final Presence nearbyEntityPresence;
    public final String nearbyEntityType;
    public final float nearestEntityDistance;
    public final boolean isDark;
    public final float daylight;
    public final int healthPercent;
    public final int ammoCount;

    public SensoryFrame() {
        this.worldSeed = 0L;
        this.frameTick = 0;
        this.playerX = 0f; this.playerY = 0f; this.playerZ = 0f;
        this.entityX = 0f; this.entityY = 0f; this.entityZ = 0f;
        this.dimensionId = 0;
        this.nearbyEntityPresence = Presence.NONE;
        this.nearbyEntityType = null;
        this.nearestEntityDistance = Float.MAX_VALUE;
        this.isDark = false;
        this.daylight = 0f;
        this.healthPercent = 100;
        this.ammoCount = 0;
    }

    /**
     * 从原始 SensoryFrame 拷贝构造（用于线程安全传递）
     */
    public SensoryFrame(SensoryFrame source) {
        this.worldSeed = source.worldSeed;
        this.frameTick = source.frameTick;
        this.playerX = source.playerX; this.playerY = source.playerY; this.playerZ = source.playerZ;
        this.entityX = source.entityX; this.entityY = source.entityY; this.entityZ = source.entityZ;
        this.dimensionId = source.dimensionId;
        this.nearbyEntityPresence = source.nearbyEntityPresence;
        this.nearbyEntityType = source.nearbyEntityType;
        this.nearestEntityDistance = source.nearestEntityDistance;
        this.isDark = source.isDark;
        this.daylight = source.daylight;
        this.healthPercent = source.healthPercent;
        this.ammoCount = source.ammoCount;
    }

    @Override
    public String toString() {
        return String.format("SensoryFrame[t=%d dim=%d pos=(%.1f,%.1f,%.1f) entity=%s]",
                frameTick, dimensionId, entityX, entityY, entityZ, nearbyEntityPresence);
    }
}
