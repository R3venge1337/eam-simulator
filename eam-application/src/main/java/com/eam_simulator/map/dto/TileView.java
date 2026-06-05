package com.eam_simulator.map.dto;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;

public record TileView(Coordinates coords, TerrainType terrain,
                       int elevation,
                       PassageType passageType,
                       boolean walkable) {
}
