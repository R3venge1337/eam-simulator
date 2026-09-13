package com.eam_simulator.building;

import com.eam_simulator.domain.DomainErrorMessages;
import com.eam_simulator.domain.building.StructureType;
import com.eam_simulator.domain.building.event.BuildingCreatedEvent;
import com.eam_simulator.domain.building.exceptions.ResourceRangeException;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.ResourceType;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

class Storehouse extends Building {
    private final Map<ResourceType, Integer> storage;

    public Storehouse(UUID mapId, Coordinates coordinates, int ownerId, int health, int maxHealth, Map<ResourceType, Integer> storage) {
        super(mapId, coordinates, ownerId, StructureType.STOREHOUSE, health, maxHealth);
        this.storage = new EnumMap<>(ResourceType.class);
        initializeEmptyStorage();
        this.registerEvent(new BuildingCreatedEvent(super.getId(), mapId, coordinates, ownerId, health, storage, Instant.now()));
    }

    public Storehouse(UUID id, UUID mapId, Coordinates coordinates, int ownerId, int health, int maxHealth, Map<ResourceType, Integer> storage, Instant occuredOn) {
        super(id, mapId, coordinates, ownerId, StructureType.STOREHOUSE, health, maxHealth, occuredOn);
        this.storage = new EnumMap<>(storage);
    }

    private void initializeEmptyStorage() {
        for (ResourceType resource : ResourceType.values()) {
            if (resource != ResourceType.NONE) {
                this.storage.put(resource, 0);
            }
        }
    }

    public void depositResource(ResourceType resource, int amount) {
        if (amount < 0) throw new ResourceRangeException(DomainErrorMessages.INVALID_RESOURCE_RANGE);
        if (resource == ResourceType.NONE) return;

        int currentAmount = this.storage.getOrDefault(resource, 0);
        this.storage.put(resource, currentAmount + amount);
    }

    public Map<ResourceType, Integer> getStorage() {
        return Map.copyOf(storage);
    }
}
