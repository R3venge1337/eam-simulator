package com.eam_simulator.map;

public class MountainGenerationContext {
    private final boolean enabled;
    private final int numberOfRanges;
    private final double maxMountainPercentage;

    private int generatedMountainTiles = 0;
    private int generatedRangesCount = 0;
    private int createdRavinesCount = 0;
    private int createdBridgesCount = 0;

    public MountainGenerationContext(boolean enabled, int numberOfRanges, double maxMountainPercentage) {
        this.enabled = enabled;
        this.numberOfRanges = numberOfRanges;
        this.maxMountainPercentage = maxMountainPercentage;
    }

    public boolean shouldGenerateNextRange(int totalTiles) {
        return enabled && (generatedRangesCount < numberOfRanges) && canGenerateMoreTiles(totalTiles);
    }

    public void incrementRangesCount() {
        this.generatedRangesCount++;
    }

    public int getGeneratedRangesCount() {
        return generatedRangesCount;
    }

    public boolean canGenerateMoreTiles(int totalTiles) {
        if (!enabled || totalTiles <= 0) return false;
        double currentFraction = (double) generatedMountainTiles / totalTiles;
        return currentFraction < maxMountainPercentage;
    }

    public void registerMountainTile() {
        this.generatedMountainTiles++;
    }

    public boolean canCreateMoreRavines(int limit) {
        return this.createdRavinesCount < limit;
    }

    public boolean canCreateMoreBridges(int limit) {
        return this.createdBridgesCount < limit;
    }

    public void registerRavine(boolean withBridge) {
        this.createdRavinesCount++;
        if (withBridge) {
            this.createdBridgesCount++;
        }
    }

    public int getGeneratedMountainTiles() { return generatedMountainTiles; }
    public int getCreatedRavinesCount() { return createdRavinesCount; }
    public int getCreatedBridgesCount() { return createdBridgesCount; }
}
