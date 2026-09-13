package com.eam_simulator.map;

import com.eam_simulator.domain.building.StructureType;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.ResourceType;

import java.util.Map;

public record StartingStructureData(Coordinates coordinates,
                                    StructureType type,
                                    int ownerId,
                                    int health,
                                    int maxHealth,
                                    Map<ResourceType, Integer> startingResources) {
}
