package com.eam_simulator.engine;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Getter
class GameEngineMetrics {
    private final AtomicLong totalTicks = new AtomicLong(0);
    private final AtomicLong lastFps = new AtomicLong(0);
    private final AtomicReference<Double> lastTickDurationMs = new AtomicReference<>(0.0);

    private long frameCount = 0;
    private long lastResetTime = System.nanoTime();

    public void recordTick(long durationNs) {
        totalTicks.incrementAndGet();
        lastTickDurationMs.set(durationNs / 1_000_000.0);

        frameCount++;
        long now = System.nanoTime();
        if (now - lastResetTime >= 1_000_000_000L) { // Co sekundę aktualizujemy FPS
            lastFps.set(frameCount);
            frameCount = 0;
            lastResetTime = now;
        }
    }
}
