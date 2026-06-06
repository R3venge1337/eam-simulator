package com.eam_simulator.domain.map.event;

import com.eam_simulator.domain.DomainEvent;
import com.eam_simulator.domain.map.entities.GenerationStatus;

import java.time.Instant;
import java.util.UUID;

public record MapEnvironmentGeneratedEvent(UUID mapId, GenerationStatus status,
                                           Instant occurredOn) implements DomainEvent {

    public MapEnvironmentGeneratedEvent(UUID mapId, GenerationStatus status) {
        this(mapId, status, Instant.now());
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
}
