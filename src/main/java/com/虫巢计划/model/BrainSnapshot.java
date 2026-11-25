package com.虫巢计划.model;

/**
 * Результат нейросети на один такт. Две копии (volatile head/tail)
 * позволяют брейну писать «следующий» в tail, а миру читать «текущий» из head.
 */
public class BrainSnapshot {

    public enum MotorAction { IDLE, MOVE_FORWARD, MOVE_BACKWARD, STRAFE_LEFT, STRAFE_RIGHT, JUMP, ATTACK }

    public long producingTick;
    public boolean valid;

    public float moveForward;      // -1..1
    public float strafe;           // -1..1
    public boolean jump;
    public boolean attack;
    public MotorAction action;
    public float confidence;
    public String debugReason;

    public BrainSnapshot copy() {
        BrainSnapshot s = new BrainSnapshot();
        s.producingTick = this.producingTick;
        s.valid = this.valid;
        s.moveForward = this.moveForward;
        s.strafe = this.strafe;
        s.jump = this.jump;
        s.attack = this.attack;
        s.action = this.action;
        s.confidence = this.confidence;
        s.debugReason = this.debugReason;
        return s;
    }
}