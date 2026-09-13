package com.eam_simulator.map.dto;

import java.util.List;

public record CreateMapResponse(String mapName, MapDimension size, TileView[][] tiles, List<StructureView> structures, List<UnitsView> units) {
}
