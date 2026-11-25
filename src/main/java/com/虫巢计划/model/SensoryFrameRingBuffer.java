package com.虫巢计划.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 无锁环形缓冲区 - 主线程生产，脑线程消费
 * 容量固定，溢出时丢弃最旧帧
 */
public class SensoryFrameRingBuffer {
    private final SensoryFrame[] buffer;
    private final int capacity;
    private final AtomicInteger writeIndex = new AtomicInteger(0);
    private final AtomicInteger readIndex = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicInteger droppedFrames = new AtomicInteger(0);
    
    public SensoryFrameRingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new SensoryFrame[capacity];
        for (int i = 0; i < capacity; i++) {
            buffer[i] = new SensoryFrame();
        }
    }
    
    /**
     * 主线程调用 - 投递感官帧
     * 若缓冲区满，丢弃最旧帧
     */
    public void offer(SensoryFrame frame) {
        int w = writeIndex.get();
        if (count.get() >= capacity) {
            // 缓冲区满，覆盖最旧帧
            readIndex.incrementAndGet();
            droppedFrames.incrementAndGet();
            count.set(capacity);
        }
        // 复制帧数据
        buffer[w] = frame.clone();
        writeIndex.set((w + 1) % capacity);
        count.incrementAndGet();
    }
    
    /**
     * 脑线程调用 - 取出最新帧
     * 若无可用帧，返回null
     */
    public SensoryFrame poll() {
        int c = count.get();
        if (c <= 0) {
            return null;
        }
        int r = readIndex.get();
        SensoryFrame frame = buffer[r];
        readIndex.set((r + 1) % capacity);
        count.decrementAndGet();
        return frame;
    }
    
    /**
     * 获取当前帧数
     */
    public int size() {
        return count.get();
    }
    
    /**
     * 获取丢弃帧数（调试用）
     */
    public int getDroppedFrames() {
        return droppedFrames.get();
    }
    
    /**
     * 重置统计
     */
    public void resetStats() {
        droppedFrames.set(0);
    }
}
