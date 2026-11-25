package com.虫巢计划;

import com.虫巢计划.model.SensoryFrame;
import com.虫巢计划.model.BrainSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Фоновый поток, где «живёт» мозг.
 * - читает SensoryFrame каждые 50ms,
 * - пишет BrainSnapshot в double-buffer (volatile head/tail).
 * Поток стартует синхронно при andнициализации мода и завершается при онлайдтейле.
 */
public class MotherBrainThread extends Thread {

    public static final Logger LOGGER = LoggerFactory.getLogger(MotherBrainThread.class);
    private static final long CYCLE_MS = 50L;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // double buffer
    private final BrainSnapshot snapshotA = new BrainSnapshot();
    private final BrainSnapshot snapshotB = new BrainSnapshot();
    // volatile: мир всегда читает актуальный head, brain пишет в tail
    private volatile BrainSnapshot head = snapshotA;
    private volatile BrainSnapshot tail = snapshotB;

    private final ReentrantLock modelLock = new ReentrantLock();
    // placeholder for injected model/provider — пока заглушка
    private Object brainModel;
    
    // 无锁环形缓冲区
    public final SensoryFrameRingBuffer frameBuffer = new SensoryFrameRingBuffer(64);
    
    // ScheduledExecutorService 替代 Thread.sleep
    private ScheduledExecutorService scheduler;

    public void startBrain() {
        if (running.compareAndSet(false, true)) {
            // 使用 ScheduledExecutorService 替代 Thread.sleep
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

    /** Главный поток вызывает каждый тик - 投递到环形缓冲区 */
    public void feed(SensoryFrame frame) {
        if (!running.get()) return;
        tickCounter.incrementAndGet();
        frameBuffer.offer(frame);
    }

    /** Мир читает head, не блокируя запись в tail. */
    public BrainSnapshot readSnapshot() {
        return head;
    }

    /** Подмена модели — можно ставить после старта. */
    public void setModel(Object model) {
        modelLock.lock();
        try {
            this.brainModel = model;
        } finally {
            modelLock.unlock();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * 脑线程主循环 - 由 ScheduledExecutorService 调度
     */
    private void brainCycle() {
        if (!running.get()) return;
        try {
            // 从环形缓冲区取出最新帧
            SensoryFrame frame = frameBuffer.poll();
            if (frame == null) {
                return; // 无新帧，跳过
            }
            
            // TODO: 接入真实神经网络推理
            // 当前使用占位逻辑：输出IDLE指令
            
            BrainSnapshot next = tail.copy();
            next.producingTick = tickCounter.get();
            next.valid = true;
            next.action = BrainSnapshot.MotorAction.IDLE;
            next.confidence = 0.5f;
            next.moveForward = 0.0f;
            next.strafe = 0.0f;
            next.jump = false;
            next.attack = false;
            
            // 原子切换双缓冲
            BrainSnapshot swap = this.head;
            this.head = next;
            this.tail = swap;
            
        } catch (Exception e) {
            LOGGER.error("Brain cycle error", e);
        }
    }
}