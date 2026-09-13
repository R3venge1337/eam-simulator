package com.eam_simulator.map;

import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapGenerationContext {
    private final int totalTiles;
    private final Map<Class<?>, Object> subContexts = new HashMap<>();
    private final TileSnapshot[][] gridSnapshot;
    private final int width;
    private final int height;

    public MapGenerationContext(int width, int height) {
        this.totalTiles = width * height;
        this.width = width;
        this.height = height;
        this.gridSnapshot = new TileSnapshot[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gridSnapshot[x][y] = new TileSnapshot(TerrainType.GRASS, 0,PassageType.FREE,true);
            }
        }
    }

    public <T> void register(Class<T> type, T context) {
        this.subContexts.put(type, context);
    }

    public <T> Optional<T> get(Class<T> type) {
        Object context = subContexts.get(type);
        if (context == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(context));
    }

    public int getTotalTiles() {
        return totalTiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void updateSnapshot(int x, int y, TerrainType terrain, int elevation, PassageType passageType, boolean isWalkable) {
        this.gridSnapshot[x][y] = new TileSnapshot(terrain, elevation, passageType, isWalkable);
    }

    public TileSnapshot getTileSnapshot(int x, int y) {
        return this.gridSnapshot[x][y];
    }
}
