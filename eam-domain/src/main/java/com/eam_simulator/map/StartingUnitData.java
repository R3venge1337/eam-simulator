package com.eam_simulator.map;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.unit.UnitType;

public record StartingUnitData(Coordinates coordinates,
                               UnitType type,
                               int ownerId) {
}
