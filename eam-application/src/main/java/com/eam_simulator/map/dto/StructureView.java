package com.eam_simulator.map.dto;

import com.eam_simulator.domain.building.StructureType;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.Dimensions;
import com.eam_simulator.domain.map.entities.ResourceType;

import java.util.Map;
import java.util.UUID;

public record StructureView(UUID id,
                            int ownerId,
                            StructureType type,
                            Coordinates coordinates,
                            Dimensions dimensions,
                            int health,
                            int maxHealth,
                            Map<ResourceType, Integer> startingResources
                            ) {
}
