package com.eam_simulator.map;

import com.eam_simulator.engine.entities.Coordinates;
import com.eam_simulator.engine.entities.PassageType;
import com.eam_simulator.engine.entities.ResourceType;
import com.eam_simulator.engine.entities.TerrainType;

class Tile {
    private final Coordinates coords;
    private TerrainType terrain;
    private ResourceType resource;
    private PassageType passage;

    private int resourceAmount;
    private int elevation;
    private Integer ownerId;

    private boolean walkable;
    private boolean isPrepared;
    private boolean isReserved;

    Tile(Coordinates coords) {
        this.coords = coords;
        this.terrain = TerrainType.GRASS;
        this.resource = ResourceType.NONE;
        this.passage = null;
        this.walkable = true;
    }

    boolean isWalkable() { return walkable; }

    void changeTerrain(TerrainType terrain) {
        this.terrain = terrain;
        this.walkable = (terrain != TerrainType.WATER) && (terrain != TerrainType.MOUNTAIN);
    }

    public Coordinates getCoords() {
        return coords;
    }
}
