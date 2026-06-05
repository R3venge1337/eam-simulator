package com.eam_simulator.map;

public class LakesGenerationContext {
    private final boolean enabled;
    private final int targetRiversCount;
    private final double maxWaterPercentage;

    private int generatedRiversCount = 0;
    private int waterTilesCount = 0;

    public LakesGenerationContext(boolean enabled, int targetRiversCount, double maxWaterPercentage) {
        this.enabled = enabled;
        this.targetRiversCount = targetRiversCount;
        this.maxWaterPercentage = maxWaterPercentage;
    }

    public boolean shouldGenerateNextRiver() {
        return enabled && generatedRiversCount < targetRiversCount;
    }

    public boolean canGenerateMoreTiles(int totalTiles) {
        if (totalTiles <= 0) {
            return false;
        }

        double currentPercentage = ((double) waterTilesCount / totalTiles);
        return currentPercentage < maxWaterPercentage;
    }

    public void incrementRiversCount() {
        this.generatedRiversCount++;
    }

    public void registerWaterTile() {
        this.waterTilesCount++;
    }

    public int getGeneratedRiversCount() {
        return generatedRiversCount;
    }

    public int getWaterTilesCount() {
        return waterTilesCount;
    }
}
