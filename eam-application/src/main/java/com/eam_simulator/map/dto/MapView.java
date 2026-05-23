package com.eam_simulator.map.dto;

public record MapView(String mapName, MapDimension size, TileView[][] tiles) {
}
