package com.eam_simulator.domain.building.event;

import com.eam_simulator.domain.DomainEvent;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.ResourceType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record BuildingCreatedEvent(UUID buildingId, UUID mapId, Coordinates coordinates, int ownerId, int health, Map<ResourceType, Integer> storage, Instant occuredOn) implements DomainEvent {
    @Override
    public Instant getOccurredOn() {
        return occuredOn;
    }
}
