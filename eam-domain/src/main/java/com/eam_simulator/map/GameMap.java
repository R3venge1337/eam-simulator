package com.eam_simulator.map;

import com.eam_simulator.engine.BaseAggregateRoot;
import com.eam_simulator.engine.entities.Coordinates;
import com.eam_simulator.engine.event.MapCreatedEvent;

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

    public MapName getMapName() {
        return mapName;
    }

    public MapSize getSize() {
        return size;
    }

    public Tile[][] getGrid() {
        return grid;
    }
}
