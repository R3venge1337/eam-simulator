package com.eam_simulator.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
class GameLoop {
    private final List<Tickable> tickables;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final GameEngineMetrics metrics;

    private static final int MAX_CATCH_UP_TICKS = 5;
    private static final double TARGET_FPS = 60.0;
    private static final double NS_PER_TICK = 1_000_000_000.0 / TARGET_FPS;

    private long tickCount = 0;

    void runLoop() {
        if (running.getAndSet(true)) {
            log.warn("GameLoop: Loop is already running!");
            return;
        }

        long lastTime = System.nanoTime();
        double delta = 0;

        log.info("GameLoop: Engine started. Target: {} FPS", TARGET_FPS);

        while (running.get() && !Thread.currentThread().isInterrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / NS_PER_TICK;
            lastTime = now;

            if (delta >= 1) {
                int ticksProcessed = 0;
                while (delta >= 1 && ticksProcessed < MAX_CATCH_UP_TICKS) {
                    processTick();
                    delta--;
                    ticksProcessed++;
                }

                if (delta > 1) {
                    log.debug("GameLoop: Skipping {} late ticks to maintain stability", (int)delta);
                    delta = 0;
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("GameLoop: Main loop exited.");
    }

    private void processTick() {
        long startTime = System.nanoTime();
        tickCount++;

        TickEmitted tick = new TickEmitted(tickCount, 1.0 / TARGET_FPS, Instant.now());

        for (Tickable tickable : tickables) {
            try {
                tickable.onTick(tick);
            } catch (Exception e) {
                log.error("GameLoop: Error in tickable {} at tick {}",
                        tickable.getClass().getSimpleName(), tickCount, e);
            }
        }

        metrics.recordTick(System.nanoTime() - startTime);
    }

    void stop() {
        running.set(false);
    }
}
