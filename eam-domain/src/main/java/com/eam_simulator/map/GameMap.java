package com.eam_simulator.map;

import com.eam_simulator.domain.BaseAggregateRoot;
import com.eam_simulator.domain.DomainErrorMessages;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.GenerationStatus;
import com.eam_simulator.domain.map.event.MapCreatedEvent;
import com.eam_simulator.domain.map.event.MapEnvironmentGeneratedEvent;
import com.eam_simulator.domain.map.exceptions.InvalidCoordinatesException;
import com.eam_simulator.domain.map.exceptions.InvalidElevationLevelException;

import java.util.UUID;

class GameMap extends BaseAggregateRoot {
    private final MapName mapName;
    private final MapSize size;
    private final Tile[][] grid;

    GameMap(MapName mapName, MapSize size) {
        this.mapName = mapName;
        this.size = size;
        this.grid = new Tile[size.width()][size.height()];

        for (int x = 0; x < size.width(); x++) {
            for (int y = 0; y < size.height(); y++) {
                this.grid[x][y] = new Tile(new Coordinates(x, y));
            }
        }
        this.registerEvent(new MapCreatedEvent(super.getId(), mapName.name(), size.width(), size.height()));
    }

    GameMap(UUID id, MapName mapName, MapSize size, Tile[][] grid) {
        super(id);
        this.mapName = mapName;
        this.size = size;
        this.grid = grid;
    }

    public void applyModification(TerrainModification modification) {
        if (modification == null) {
            return;
        }

        Coordinates coords = modification.coordinates();

        if (isOutOfBounds(coords)) {
            throw new InvalidCoordinatesException(coords.x(), coords.y());
        }

        if (modification.elevation() < 0) {
            throw new InvalidElevationLevelException(DomainErrorMessages.INVALID_ELEVATION_LEVEL);
        }

        Tile tile = this.grid[coords.x()][coords.y()];
        tile.shapeTerrain(modification.terrainType(), modification.elevation(), modification.passageType(), modification.isWalkable());
    }

    private boolean isOutOfBounds(Coordinates coords) {
        return coords.x() < 0 || coords.x() >= size.width() ||
                coords.y() < 0 || coords.y() >= size.height();
    }

    public void startEnvironmentGeneration() {
        this.registerEvent(new MapEnvironmentGeneratedEvent(super.getId(), GenerationStatus.UNDER_CONSTRUCTION));
    }

    public void completeEnvironmentGeneration() {
        this.registerEvent(new MapEnvironmentGeneratedEvent(super.getId(), GenerationStatus.CREATED));
    }

    public void failedEnvironmentGeneration() {
        this.registerEvent(new MapEnvironmentGeneratedEvent(super.getId(), GenerationStatus.FAILED));
    }

    public MapName getMapName() {
        return mapName;
    }

    public MapSize getSize() {
        return size;
    }

    Tile[][] getGrid() {
        return grid;
    }
}
