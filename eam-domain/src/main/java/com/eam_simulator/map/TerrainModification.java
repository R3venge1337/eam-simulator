package com.eam_simulator.map;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;

public record TerrainModification(Coordinates coordinates, TerrainType terrainType, int elevation, PassageType passageType, boolean isWalkable){}
