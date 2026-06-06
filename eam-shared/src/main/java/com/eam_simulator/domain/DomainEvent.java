package com.eam_simulator.domain;

import java.time.Instant;

public interface DomainEvent {
    Instant getOccurredOn();
}
