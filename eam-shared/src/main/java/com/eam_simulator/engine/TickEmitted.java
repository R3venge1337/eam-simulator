package com.eam_simulator.engine;

import java.time.Instant;

public record TickEmitted(long number, double deltaTime, Instant timestamp) {
}
