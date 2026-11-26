package com.虫巢计划.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 意图接口 - 脑线程输出的高层决策
 * 
 * 线程安全约束：
 * - 禁止持有 Entity 引用（Entity 生命周期不可预测）
 * - 目标标识使用 UUID
 * - 所有实现类应为不可变设计
 */
public sealed interface Intent permits ExploreIntent, AttackIntent, GatherIntent, ReturnHomeIntent, IdleIntent {

    /**
     * 执行优先级（0-1，越高越优先）
     */
    float priority();

    /**
     * 意图类型描述
     */
    String type();
}

/**
 * 探索意图 - 搜索资源或目标
 */
record ExploreIntent(UUID targetLocationId, float radius, long timestamp) implements Intent {
    @Override
    public float priority() { return 0.3f; }
    
    @Override
    public String type() { return "EXPLORE"; }
}

/**
 * 攻击意图 - 攻击指定实体
 */
record AttackIntent(UUID targetEntityId, float maxRange, long timestamp) implements Intent {
    @Override
    public float priority() { return 0.8f; }
    
    @Override
    public String type() { return "ATTACK"; }
}

/**
 * 采集意图 - 收集资源
 */
record GatherIntent(UUID resourceId, int amount, long timestamp) implements Intent {
    @Override
    public float priority() { return 0.5f; }
    
    @Override
    public String type() { return "GATHER"; }
}

/**
 * 返回巢穴意图 - 回归母虫位置
 */
record ReturnHomeIntent(long timestamp) implements Intent {
    @Override
    public float priority() { return 0.6f; }
    
    @Override
    public String type() { return "RETURN_HOME"; }
}

/**
 * 空闲意图 - 无明确目标
 */
record IdleIntent(long timestamp) implements Intent {
    @Override
    public float priority() { return 0.0f; }
    
    @Override
    public String type() { return "IDLE"; }
}
