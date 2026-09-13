package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.MountainGenerationSettings;
import com.eam_simulator.map.dto.RavineGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(1)
class MountainAndRavineGeneratorStep implements MapGenerationStep {
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        Optional<MountainGenerationSettings> settingsOpt = context.get(MountainGenerationSettings.class);
        Optional<MountainGenerationContext> mountainCtxOpt = context.get(MountainGenerationContext.class);

        if (settingsOpt.isEmpty() || mountainCtxOpt.isEmpty() || !settingsOpt.get().enableMountains()) {
            return List.of();
        }

        MountainGenerationSettings settings = settingsOpt.get();
        MountainGenerationContext mountainContext = mountainCtxOpt.get();

        RavineGenerationSettings ravineSettings = settings.ravineSettings();
        boolean ravinesEnabled = ravineSettings != null && ravineSettings.enableRavines();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        // Rejestr unikalnych modyfikacji w tym kroku: Coordinates -> TerrainModification
        Map<Coordinates, TerrainModification> mountainMap = new HashMap<>();

        int maxAttempts = 100;
        int attempts = 0;

        while (mountainContext.shouldGenerateNextRange(totalTiles) && attempts < maxAttempts) {
            attempts++;

            // 1. Losowanie grani pasma górskiego (początek i koniec)
            int startX = random.nextInt(width);
            int startY = random.nextInt(height);

            int ridgeLength = random.nextInt(settings.maxRidgeLength() - settings.minRidgeLength() + 1) + settings.minRidgeLength();
            double angle = random.nextDouble() * Math.PI * 2;

            int endX = (int) (startX + Math.cos(angle) * ridgeLength);
            int endY = (int) (startY + Math.sin(angle) * ridgeLength);

            // 2. Czy w tym paśmie ma powstać jar / przełęcz / most?
            boolean hasRavinePass = ravinesEnabled && mountainContext.canCreateMoreRavines(ravineSettings.numberOfRavines());
            double ravineLocation = 0.4 + (random.nextDouble() * 0.2); // Przełęcz w 40-60% długości pasma
            double ravineHalfWidth = (double) ravineSettings.ravineWidth() / 2.0;
            boolean spawnWithBridge = hasRavinePass && mountainContext.canCreateMoreBridges(ravineSettings.maxBridges());

            int minX = Math.max(0, Math.min(startX, endX) - settings.baseRadius());
            int maxX = Math.min(width - 1, Math.max(startX, endX) + settings.baseRadius());
            int minY = Math.max(0, Math.min(startY, endY) - settings.baseRadius());
            int maxY = Math.min(height - 1, Math.max(startY, endY) + settings.baseRadius());

            boolean ravineRegisteredForThisRange = false;
            boolean addedNewTile = false;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {

                    double distanceToRidge = distanceToLineSegment(x, y, startX, startY, endX, endY);

                    if (distanceToRidge <= settings.baseRadius()) {
                        Coordinates coords = new Coordinates(x, y);

                        // --- OBSŁUGA JARU / PRZEŁĘCZY / MOSTEKU ---
                        if (hasRavinePass) {
                            double progressOnLine = getProgressOnLineSegment(x, y, startX, startY, endX, endY);
                            double distanceFromRavineCenter = Math.abs(progressOnLine - ravineLocation) * ridgeLength;

                            if (distanceFromRavineCenter < ravineHalfWidth && distanceToRidge <= ravineSettings.ravineLength()) {
                                PassageType passage = spawnWithBridge ? PassageType.BRIDGE : PassageType.BLOCKED;
                                boolean walkable = spawnWithBridge;

                                TerrainModification ravineMod = new TerrainModification(
                                        coords,
                                        TerrainType.RAVINE,
                                        0, // Jar wycina teren do poziomu 0
                                        passage,
                                        walkable
                                );

                                if (!mountainMap.containsKey(coords)) {
                                    mountainContext.registerMountainTile();
                                    addedNewTile = true;
                                }
                                mountainMap.put(coords, ravineMod);

                                if (!ravineRegisteredForThisRange) {
                                    mountainContext.registerRavine(spawnWithBridge);
                                    ravineRegisteredForThisRange = true;
                                }
                                continue;
                            }
                        }

                        // --- OBSŁUGA MASYWU GÓRSKIEGO (Profil wysokościowy) ---
                        double relativeDist = distanceToRidge / settings.baseRadius();

                        int targetElevation;
                        TerrainType targetTerrain;

                        if (relativeDist <= 0.25) {
                            targetElevation = 3;
                            targetTerrain = TerrainType.SNOW;
                        } else if (relativeDist <= 0.65) {
                            targetElevation = 2;
                            targetTerrain = TerrainType.MOUNTAIN;
                        } else if (relativeDist <= 0.85) {
                            targetElevation = 1;
                            targetTerrain = TerrainType.HILL;
                        } else {
                            targetElevation = 1;
                            targetTerrain = TerrainType.UNEVEN_GROUND;
                        }

                        // Górskie kafelki są domyślnie nieprzejezdne (BLOCKED)
                        TerrainModification mountainMod = new TerrainModification(
                                coords,
                                targetTerrain,
                                targetElevation,
                                PassageType.BLOCKED,
                                false
                        );

                        // Jeśli kafelek już istnieje, wybieramy wyższą wysokość (masywy łączą się naturalnie)
                        TerrainModification existing = mountainMap.get(coords);
                        if (existing == null) {
                            mountainMap.put(coords, mountainMod);
                            mountainContext.registerMountainTile();
                            addedNewTile = true;
                        } else if (existing.terrainType() != TerrainType.RAVINE && targetElevation > existing.elevation()) {
                            mountainMap.put(coords, mountainMod);
                        }
                    }
                }
            }

            if (addedNewTile) {
                attempts = 0; // Reset prób przy pomyślnym dodaniu nowego terenu
            }
            mountainContext.incrementRangesCount();
        }

        return new ArrayList<>(mountainMap.values());
    }

    private double distanceToLineSegment(int px, int py, int x1, int y1, int x2, int y2) {
        double l2 = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        if (l2 == 0) return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));

        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2;
        t = Math.max(0, Math.min(1, t));

        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);

        return Math.sqrt(Math.pow(px - projX, 2) + Math.pow(py - projY, 2));
    }

    private double getProgressOnLineSegment(int px, int py, int x1, int y1, int x2, int y2) {
        double l2 = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        if (l2 == 0) return 0.0;
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2;
        return Math.max(0, Math.min(1, t));
    }
}
