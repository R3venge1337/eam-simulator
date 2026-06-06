package com.eam_simulator.map;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.ResourceType;
import com.eam_simulator.domain.map.entities.TerrainType;

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

    public Tile(Coordinates coords, TerrainType terrain, int elevation, PassageType passageType, boolean walkable) {
        this.coords = coords;
        this.terrain = terrain;
        this.elevation = elevation;
        this.walkable = walkable;
    }

    void shapeTerrain(TerrainType terrain, int elevation, PassageType passageType, boolean isWalkable) {
        this.terrain = terrain;
        this.elevation = elevation;
        this.passage = passageType;
        this.walkable = isWalkable;
    }

    TerrainType getTerrain() {
        return terrain;
    }

    int getElevation() {
        return elevation;
    }

    boolean isWalkable() {
        return walkable;
    }

    public Coordinates getCoords() {
        return coords;
    }

    PassageType getPassage() {
        return passage;
    }
}
