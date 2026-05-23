package com.eam_simulator.map;

import com.eam_simulator.engine.exceptions.InvalidMapSizeException;

record MapSize(int width, int height) {
    public MapSize {
        if (width <= 0 || height <= 0) {
            throw new InvalidMapSizeException(width, height);
        }
    }

    public int totalTiles() {
        return width * height;
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
