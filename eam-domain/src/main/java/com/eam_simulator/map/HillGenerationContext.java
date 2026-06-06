package com.eam_simulator.map;

public class HillGenerationContext {
    private final boolean enabled;
    private final double maxHillPercentage;
    private int hillTilesCount = 0;

    public HillGenerationContext(boolean enabled, double maxHillPercentage) {
        this.enabled = enabled;
        this.maxHillPercentage = maxHillPercentage;
    }

    public boolean canGenerateMoreTiles(int totalTiles) {
        if (!enabled || totalTiles <= 0) {
            return false;
        }
        return ((double) hillTilesCount / totalTiles) < maxHillPercentage;
    }

    public void registerHillTile() {
        this.hillTilesCount++;
    }

    public int getHillTilesCount() {
        return hillTilesCount;
    }
}
