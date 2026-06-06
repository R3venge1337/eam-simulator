package com.eam_simulator.map;

public class SandGenerationContext {
    private int generatedSandTiles = 0;

    public void registerSandTile() {
        this.generatedSandTiles++;
    }

    public int getGeneratedSandTiles() {
        return generatedSandTiles;
    }
}
