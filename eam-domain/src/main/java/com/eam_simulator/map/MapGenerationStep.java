package com.eam_simulator.map;

import java.util.List;

public interface MapGenerationStep {
    List<TerrainModification> execute(MapGenerationContext context);
}
