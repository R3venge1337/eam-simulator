package com.eam_simulator.building;

import com.eam_simulator.domain.BaseAggregateRoot;
import com.eam_simulator.domain.building.StructureType;
import com.eam_simulator.domain.map.entities.Coordinates;

import java.time.Instant;
import java.util.UUID;

class Building extends BaseAggregateRoot {
    private final UUID mapId;
    private final Coordinates coordinates;
    private final int ownerId;
    private final StructureType type;
    private int health;
    private final int maxHealth;
    private boolean isDestroyed;

    protected Building(UUID mapId, Coordinates coordinates, int ownerId, StructureType type, int health, int maxHealth) {
        super();
        this.mapId = mapId;
        this.coordinates = coordinates;
        this.ownerId = ownerId;
        this.type = type;
        this.health = health;
        this.maxHealth = maxHealth;
    }

    protected Building(UUID id, UUID mapId, Coordinates coordinates, int ownerId, StructureType type, int health, int maxHealth, Instant occuredOn) {
        super(id);
        this.mapId = mapId;
        this.coordinates = coordinates;
        this.ownerId = ownerId;
        this.type = type;
        this.health = health;
        this.maxHealth = maxHealth;
    }

}
