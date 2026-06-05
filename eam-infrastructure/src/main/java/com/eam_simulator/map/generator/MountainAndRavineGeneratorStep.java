package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.MountainGenerationSettings;
import com.eam_simulator.map.dto.RavineGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Order(1)
class MountainAndRavineGeneratorStep implements MapGenerationStep {
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        Optional<MountainGenerationSettings> settingsOpt = context.get(MountainGenerationSettings.class);
        if (settingsOpt.isEmpty() || !settingsOpt.get().enableMountains()) {
            return modifications;
        }
        MountainGenerationSettings settings = settingsOpt.get();

        RavineGenerationSettings ravineSettings = settings.ravineSettings();
        boolean ravinesEnabled = ravineSettings != null && ravineSettings.enableRavines();

        Optional<MountainGenerationContext> mountainCtxOpt = context.get(MountainGenerationContext.class);
        if (mountainCtxOpt.isEmpty()) {
            return modifications;
        }
        MountainGenerationContext mountainContext = mountainCtxOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        boolean walkable = false;
        while (mountainContext.shouldGenerateNextRange(totalTiles)) {

            int startX = random.nextInt(width);
            int startY = random.nextInt(height);

            int ridgeLength = random.nextInt(settings.maxRidgeLength() - settings.minRidgeLength() + 1) + settings.minRidgeLength();
            double angle = random.nextDouble() * Math.PI * 2; // Losowy kierunek

            int endX = (int) (startX + Math.cos(angle) * ridgeLength);
            int endY = (int) (startY + Math.sin(angle) * ridgeLength);

            boolean hasRavinePass = ravinesEnabled && mountainContext.canCreateMoreRavines(ravineSettings.numberOfRavines());

            double ravineLocation = 0.4 + (random.nextDouble() * 0.2);

            double ravineHalfWidth = (double) ravineSettings.ravineWidth() / 2.0;

            boolean spawnWithBridge = hasRavinePass && mountainContext.canCreateMoreBridges(ravineSettings.maxBridges());

            int minX = Math.max(0, Math.min(startX, endX) - settings.baseRadius());
            int maxX = Math.min(width - 1, Math.max(startX, endX) + settings.baseRadius());
            int minY = Math.max(0, Math.min(startY, endY) - settings.baseRadius());
            int maxY = Math.min(height - 1, Math.max(startY, endY) + settings.baseRadius());

            boolean ravineRegisteredForThisRange = false;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {

                    double distanceToRidge = distanceToLineSegment(x, y, startX, startY, endX, endY);

                    if (distanceToRidge <= settings.baseRadius()) {

                        Coordinates coords = new Coordinates(x, y);

                        if (hasRavinePass) {
                            double progressOnLine = getProgressOnLineSegment(x, y, startX, startY, endX, endY);

                            double distanceFromRavineCenter = Math.abs(progressOnLine - ravineLocation) * ridgeLength;

                            if (distanceFromRavineCenter < ravineHalfWidth && distanceToRidge <= ravineSettings.ravineLength()) {

                                PassageType passage = spawnWithBridge ? PassageType.BRIDGE : null;
                                walkable = spawnWithBridge;

                                modifications.add(new TerrainModification(coords, TerrainType.RAVINE, 0, passage, walkable));

                                if (!ravineRegisteredForThisRange) {
                                    mountainContext.registerRavine(spawnWithBridge);
                                    ravineRegisteredForThisRange = true;
                                }
                                continue;
                            }
                        }
                        double relativeDist = distanceToRidge / settings.baseRadius();

                        int targetElevation;
                        TerrainType targetTerrain;
                        if (relativeDist <= 0.25) {
                            targetElevation = 3;
                            targetTerrain = TerrainType.SNOW;
                            walkable = false;
                        } else if (relativeDist <= 0.65) {
                            targetElevation = 2;
                            targetTerrain = TerrainType.MOUNTAIN;
                            walkable = false;
                        } else if (relativeDist <= 0.85) {
                            targetElevation = 1;
                            targetTerrain = TerrainType.HILL;
                            walkable = false;
                        } else {
                            targetElevation = 1;
                            targetTerrain = TerrainType.UNEVEN_GROUND;
                            walkable = false;
                        }

                        modifications.add(new TerrainModification(coords, targetTerrain, targetElevation, null, walkable));
                        mountainContext.registerMountainTile();
                    }
                }
            }

            mountainContext.incrementRangesCount();
        }

        return modifications;
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
