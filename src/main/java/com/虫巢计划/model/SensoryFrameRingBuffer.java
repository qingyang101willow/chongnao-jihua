package com.虫巢计划.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 无锁环形缓冲区 - 主线程生产，脑线程消费
 * 容量固定，溢出时丢弃最旧帧
 * 
 * 线程安全：使用 AtomicInteger 保证原子操作
 */
public class SensoryFrameRingBuffer {
    private final SensoryFrame[] buffer;
    private final int capacity;
    private final AtomicInteger writeIndex = new AtomicInteger(0);
    private final AtomicInteger readIndex = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicInteger droppedFrames = new AtomicInteger(0);
    private final AtomicInteger totalFrames = new AtomicInteger(0);
    
    public SensoryFrameRingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new SensoryFrame[capacity];
        for (int i = 0; i < capacity; i++) {
            buffer[i] = new SensoryFrame();
        }
    }
    
    public void offer(SensoryFrame frame) {
        totalFrames.incrementAndGet();
        int w = writeIndex.get();
        if (count.get() >= capacity) {
            readIndex.incrementAndGet();
            droppedFrames.incrementAndGet();
            count.set(capacity);
        }
        buffer[w] = new SensoryFrame(frame);
        writeIndex.set((w + 1) % capacity);
        count.incrementAndGet();
    }
    
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
    
    public int size() {
        return count.get();
    }
    
    public int getDroppedFrames() {
        return droppedFrames.get();
    }
    
    public int getTotalFrames() {
        return totalFrames.get();
    }
    
    public float getDropRate() {
        int total = totalFrames.get();
        if (total == 0) return 0.0f;
        return (float) droppedFrames.get() / total;
    }
    
    public void resetStats() {
        droppedFrames.set(0);
        totalFrames.set(0);
    }
}
