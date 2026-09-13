package com.eam_simulator.map.dto;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.unit.UnitType;

import java.util.UUID;

public record UnitsView(UUID id,
                        int ownerId,
                        UnitType type,
                        Coordinates coordinates) {
}
