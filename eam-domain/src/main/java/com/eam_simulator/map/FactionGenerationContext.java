package com.eam_simulator.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionGenerationContext {
    private final int totalPlayers;
    private final Map<Integer, List<StartingStructureData>> structuresByOwner = new HashMap<>();
    private final Map<Integer, List<StartingUnitData>> unitsByOwner = new HashMap<>();

    public FactionGenerationContext(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public void registerStructure(int ownerId, StartingStructureData structure) {
        this.structuresByOwner
                .computeIfAbsent(ownerId, k -> new ArrayList<>())
                .add(structure);
    }

    public void registerUnit(int ownerId, StartingUnitData unit) {
        this.unitsByOwner
                .computeIfAbsent(ownerId, k -> new ArrayList<>())
                .add(unit);
    }

    public List<StartingStructureData> getAllStructures() {
        return structuresByOwner.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public List<StartingUnitData> getAllUnits() {
        return unitsByOwner.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }
}
