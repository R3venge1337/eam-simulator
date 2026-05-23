package com.eam_simulator.map.dto;

public record CreateMapResponse(String mapName, MapDimension size, TileView[][] tiles) {
}
