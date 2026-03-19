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

    @ReadOperation
    Map<String, Object> getStatus() {
        return Map.of(
                "status", "RUNNING",
                "current_ups", metrics.getLastFps().get(),
                "avg_tick_duration_ms", String.format("%.3f", metrics.getLastTickDurationMs().get()),
                "total_ticks", metrics.getTotalTicks().get(),
                "load_percent", String.format("%.2f%%", (metrics.getLastTickDurationMs().get() / 16.67) * 100)
        );
    }
}