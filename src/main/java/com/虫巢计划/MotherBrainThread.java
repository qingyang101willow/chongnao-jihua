package com.虫巢计划;

import com.虫巢计划.model.BrainSnapshot;
import com.虫巢计划.model.Intent;
import com.虫巢计划.model.SensoryFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 脑线程 - 神经推理核心
 * 
 * 线程安全约束（严格执行）：
 * 1. 禁止直接访问任何 Minecraft World/Entity 对象
 * 2. 所有推理必须基于 SensoryFrame 快照数据
 * 3. 输出 Intent 不得持有 Entity 引用，使用 UUID
 * 
 * Brain Thread Safety Rules:
 * MotherBrainThread 不允许直接访问任何 Minecraft World/Entity 对象。
 * 所有神经推理必须基于 SensoryFrame 快照数据。
 */
public class MotherBrainThread extends Thread {

    public static final Logger LOGGER = LoggerFactory.getLogger(MotherBrainThread.class);
    private static final long CYCLE_MS = 50L;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // double buffer
    private final BrainSnapshot snapshotA = BrainSnapshot.idle(0);
    private final BrainSnapshot snapshotB = BrainSnapshot.idle(0);
    // volatile: 世界始终读取最新的 head
    private volatile BrainSnapshot head = snapshotA;
    private volatile BrainSnapshot tail = snapshotB;

    private final ReentrantLock modelLock = new ReentrantLock();
    private Object brainModel;
    
    // 无锁环形缓冲区
    public final SensoryFrameRingBuffer frameBuffer = new SensoryFrameRingBuffer(64);
    
    // ScheduledExecutorService
    private ScheduledExecutorService scheduler;

    public void startBrain() {
        if (running.compareAndSet(false, true)) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "MotherBrain");
                t.setDaemon(true);
                return t;
            });
            scheduler.scheduleAtFixedRate(this::brainCycle, 0, CYCLE_MS, TimeUnit.MILLISECONDS);
            LOGGER.info("MotherBrainThread запущен (ScheduledExecutor)");
        }
    }

    public void stopBrain() {
        running.set(false);
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("MotherBrainThread остановлен");
    }

    /**
     * 主线程调用 - 投递感官帧
     * 注意：传入的 frame 应在主线程采集完毕，此处不再访问世界
     */
    public void feed(SensoryFrame frame) {
        if (!running.get()) return;
        tickCounter.incrementAndGet();
        frameBuffer.offer(frame);
    }

    /**
     * 主线程调用 - 读取最新快照
     */
    public BrainSnapshot readSnapshot() {
        return head;
    }

    /**
     * 获取当前推理出的最高优先级意图
     */
    public Intent getCurrentIntent() {
        BrainSnapshot snap = head;
        if (!snap.valid) {
            return new IdleIntent(System.currentTimeMillis());
        }
        // 简化映射：根据 action 返回对应 Intent
        switch (snap.action) {
            case MOVE_FORWARD:
            case MOVE_BACKWARD:
            case STRAFE_LEFT:
            case STRAFE_RIGHT:
                return new ExploreIntent(null, 0, System.currentTimeMillis());
            case ATTACK:
                return new AttackIntent(null, 5f, System.currentTimeMillis());
            case JUMP:
                return new IdleIntent(System.currentTimeMillis());
            default:
                return new IdleIntent(System.currentTimeMillis());
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * 脑线程主循环 - 由 ScheduledExecutorService 调度
     * 注意：此方法运行在独立线程，禁止访问任何 Minecraft 世界对象
     */
    private void brainCycle() {
        if (!running.get()) return;
        try {
            SensoryFrame frame = frameBuffer.poll();
            if (frame == null) {
                return;
            }
            
            // TODO: 接入真实神经网络推理
            // 当前使用占位逻辑：输出 IDLE 指令
            // 严禁在此处访问 World/Entity/Chunk 等对象
            
            BrainSnapshot next = BrainSnapshot.of(
                    tickCounter.get(),
                    0.0f, 0.0f, false, false,
                    BrainSnapshot.MotorAction.IDLE,
                    0.5f,
                    "placeholder"
            );
            
            // 原子切换双缓冲
            BrainSnapshot swap = this.head;
            this.head = next;
            this.tail = swap;
            
        } catch (Exception e) {
            LOGGER.error("Brain cycle error", e);
        }
    }
}
