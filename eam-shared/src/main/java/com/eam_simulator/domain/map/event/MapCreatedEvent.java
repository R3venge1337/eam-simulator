package com.eam_simulator.domain.map.event;

import com.eam_simulator.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MapCreatedEvent(UUID mapId, String mapName, int width, int height,
                              Instant occurredOn) implements DomainEvent {

    public MapCreatedEvent(UUID mapId, String mapName, int width, int height) {
        this(mapId, mapName, width, height, Instant.now());
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
}
