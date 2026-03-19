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
    private long tickCount = 0;
    private final GameEngineMetrics metrics;

    void runLoop() {
        if (running.getAndSet(true)) {
            log.warn("Pętla gry już pracuje!");
            return;
        }

        final double targetFps = 60.0;
        final double nsPerTick = 1_000_000_000.0 / targetFps;

        long lastTime = System.nanoTime();
        double delta = 0;

        log.info("Silnik EAM wystartował. Cel: {} FPS", targetFps);

        while (running.get()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            if (delta >= 1) {
                while (delta >= 1) {
                    processTick();
                    delta--;
                }
            } else {
                Thread.onSpinWait();
            }
        }
    }

    private void processTick() {
        tickCount++;
        TickEmitted tick = new TickEmitted(tickCount, 1.0 / 60.0, Instant.now());
        tickables.forEach(tickable -> {
            try {
                tickable.onTick(tick);
            } catch (Exception e) {
                log.error("Błąd podczas przetwarzania ticka {} w obiekcie {}",
                        tickCount, tickable.getClass().getSimpleName(), e);
            }
        });
    }

    void stop() {
        log.info("Zatrzymywanie silnika gry...");
        running.set(false);
    }
}
