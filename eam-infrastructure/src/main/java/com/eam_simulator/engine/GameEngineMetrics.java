package com.eam_simulator.engine;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Getter
class GameEngineMetrics {
    private final long NANOS_PER_SECOND = 1_000_000_000L;
    private final double NANOS_PER_MILLI = 1_000_000.0;
    private final AtomicLong totalTicks = new AtomicLong(0);
    private final AtomicLong lastFps = new AtomicLong(0);
    private final AtomicReference<Double> lastTickDurationMs = new AtomicReference<>(0.0);

    private long frameCount = 0;
    private long lastResetTime = System.nanoTime();

    public synchronized void recordTick(long durationNs) {
        totalTicks.incrementAndGet();
        lastTickDurationMs.set(durationNs / NANOS_PER_MILLI);

        frameCount++;
        long now = System.nanoTime();
        long elapsedNs = now - lastResetTime;

        if (elapsedNs >= NANOS_PER_SECOND) {
            long actualFps = (frameCount * NANOS_PER_SECOND) / elapsedNs;
            lastFps.set(actualFps);

            frameCount = 0;
            lastResetTime = now;
        }
    }
}
