package com.虫巢计划.model;

/**
 * 神经推理结果快照。
 * 
 * 线程安全约束：
 * - 此对象为不可变设计（all fields final）
 * - 通过双缓冲机制在脑线程与主线程间传递
 * - 禁止任何形式的 setter
 */
public final class BrainSnapshot {

    public enum MotorAction { IDLE, MOVE_FORWARD, MOVE_BACKWARD, STRAFE_LEFT, STRAFE_RIGHT, JUMP, ATTACK }

    public final long producingTick;
    public final boolean valid;
    public final float moveForward;
    public final float strafe;
    public final boolean jump;
    public final boolean attack;
    public final MotorAction action;
    public final float confidence;
    public final String debugReason;

    /**
     * 工厂方法：构建默认 IDLE 快照
     */
    public static BrainSnapshot idle(long tick) {
        return new BrainSnapshot(tick, false, 0.0f, 0.0f, false, false, MotorAction.IDLE, 0.5f, null);
    }

    /**
     * 工厂方法：构建有效快照
     */
    public static BrainSnapshot of(long tick, float moveForward, float strafe, 
                                   boolean jump, boolean attack, MotorAction action, 
                                   float confidence, String reason) {
        return new BrainSnapshot(tick, true, moveForward, strafe, jump, attack, action, confidence, reason);
    }

    private BrainSnapshot(long producingTick, boolean valid, float moveForward, float strafe,
                          boolean jump, boolean attack, MotorAction action, float confidence, String debugReason) {
        this.producingTick = producingTick;
        this.valid = valid;
        this.moveForward = moveForward;
        this.strafe = strafe;
        this.jump = jump;
        this.attack = attack;
        this.action = action;
        this.confidence = confidence;
        this.debugReason = debugReason;
    }
}
