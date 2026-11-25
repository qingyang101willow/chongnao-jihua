package com.虫巢计划.model;

/**
 * 运动指令 - 由脑线程输出，主线程应用到实体
 */
public class MotorCommand {
    public float forward;      // -1.0 ~ 1.0
    public float strafe;       // -1.0 ~ 1.0
    public boolean jump;
    public boolean attack;
    public float confidence;   // 0.0 ~ 1.0
    
    public static final MotorCommand EMPTY = new MotorCommand();
    
    public MotorCommand() {
        this(0f, 0f, false, false, 0f);
    }
    
    public MotorCommand(float forward, float strafe, boolean jump, boolean attack, float confidence) {
        this.forward = forward;
        this.strafe = strafe;
        this.jump = jump;
        this.attack = attack;
        this.confidence = confidence;
    }
    
    /**
     * 合并两个指令（取高置信度）
     */
    public MotorCommand merge(MotorCommand other) {
        if (other.confidence > this.confidence) {
            return other;
        }
        return this;
    }
}
