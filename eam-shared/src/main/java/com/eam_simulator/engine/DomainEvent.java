package com.eam_simulator.engine;

import java.time.Instant;

public interface DomainEvent {
    Instant getOccurredOn();
}
