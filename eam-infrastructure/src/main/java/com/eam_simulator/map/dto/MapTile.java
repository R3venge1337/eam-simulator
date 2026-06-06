package com.eam_simulator.map.dto;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;

import java.io.Serializable;

public record MapTile(Coordinates coords, TerrainType terrain,
                      int elevation,
                      PassageType passageType,
                      boolean walkable) implements Serializable {

}
