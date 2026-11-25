package com.虫巢计划.model;

/**
 * Считывает и хранит сырые координаты/состояния из мира.
 * Один кадр/тик инкрементально копирует relevant state;
 * не делает запросов к сеттингу — мёрджит только то, что передано из коллбэка.
 */
public class SensoryFrame {

    public enum Presence { NONE, UNKNOWN, EMPTY, OCCUPIED }

    public long worldSeed;
    public int frameTick;
    public float playerX, playerY, playerZ;
    public float entityX, entityY, entityZ;
    public int dimensionId;
    public Presence nearbyEntityPresence;
    public String nearbyEntityType;
    public float nearestEntityDistance;
    public boolean isDark;
    public float daylight;
    public int healthPercent;
    public int ammoCount;

    public SensoryFrame clone() {
        SensoryFrame f = new SensoryFrame();
        f.worldSeed = this.worldSeed;
        f.frameTick = this.frameTick;
        f.playerX = this.playerX; f.playerY = this.playerY; f.playerZ = this.playerZ;
        f.entityX = this.entityX; f.entityY = this.entityY; f.entityZ = this.entityZ;
        f.dimensionId = this.dimensionId;
        f.nearbyEntityPresence = this.nearbyEntityPresence;
        f.nearbyEntityType = this.nearbyEntityType;
        f.nearestEntityDistance = this.nearestEntityDistance;
        f.isDark = this.isDark;
        f.daylight = this.daylight;
        f.healthPercent = this.healthPercent;
        f.ammoCount = this.ammoCount;
        return f;
    }
}