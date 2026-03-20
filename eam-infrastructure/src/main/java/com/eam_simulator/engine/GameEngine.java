package com.eam_simulator.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
class GameEngine implements GameEngineFacade {
    private final GameLoop gameLoop;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Async("gameEngineTaskExecutor")
    @Override
    public void startSimulation() {
        if (isStarted.compareAndSet(false, true)) {
            log.info("Game Engine: World is ready. Starting the main simulation loop on thread: {}",
                    Thread.currentThread().getName());
            try {
                gameLoop.runLoop();
            } catch (Exception e) {
                log.error("Game Engine: Critical error in main loop. Engine stopped.", e);
                isStarted.set(false);
            } finally {
                log.info("Game Engine: Simulation loop finished.");
            }
        } else {
            log.warn("Game Engine: Received 'World Ready' signal but engine is already running. Ignoring.");
        }
    }
    @Override
    public void stop() {
        log.info("Game Engine: Requesting shutdown...");
        gameLoop.stop();
    }

    @Override
    public boolean isRunning() {
        return isStarted.get();
    }
}
