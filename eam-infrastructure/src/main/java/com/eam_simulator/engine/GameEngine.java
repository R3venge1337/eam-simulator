package com.eam_simulator.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class GameEngine implements GameEngineFacade {
    private final GameLoop gameLoop;

    @Async("gameEngineTaskExecutor")
    @Override
    public void notifyWorldReady() {
        gameLoop.runLoop();
    }
}
