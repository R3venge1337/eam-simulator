package com.eam_simulator.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "gamestatus")
@RequiredArgsConstructor
class GameStatusEndpoint {

    private final GameEngineMetrics metrics;
    private final GameEngineFacade engineFacade;

    @ReadOperation
    Map<String, Object> getStatus() {
        double lastTickMs = metrics.getLastTickDurationMs().get();
        double TARGET_TICK_MS = 16.666666666666668;

        return Map.of(
                "status", engineFacade.isRunning() ? "RUNNING" : "STOPPED",
                "current_ups", metrics.getLastFps().get(),
                "avg_tick_duration_ms", String.format("%.3f", metrics.getLastTickDurationMs().get()),
                "total_ticks", metrics.getTotalTicks().get(),
                "load_factor", lastTickMs / TARGET_TICK_MS
        );
    }
}