package com.eam_simulator.map;

import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;

public record TileSnapshot(TerrainType terrain, int elevation, PassageType passageType, boolean isWalkable) {
}
