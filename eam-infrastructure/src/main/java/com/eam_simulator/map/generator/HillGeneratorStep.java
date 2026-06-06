package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.HillGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Order(4)
class HillGeneratorStep implements MapGenerationStep {
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();
        Optional<HillGenerationContext> hillCtxOpt = context.get(HillGenerationContext.class);

        if (hillCtxOpt.isEmpty()) return modifications;
        HillGenerationContext hillContext = hillCtxOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        Optional<HillGenerationSettings> settingsOpt = context.get(HillGenerationSettings.class);
        if (settingsOpt.isEmpty() || !settingsOpt.get().enableHills()) {
            return modifications;
        }
        HillGenerationSettings settings = settingsOpt.get();

        while (hillContext.canGenerateMoreTiles(totalTiles)) {
            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);

            int radius = random.nextInt(settings.maxHillRadius() - settings.minHillRadius() + 1) + settings.minHillRadius();

            double entranceAngle = (random.nextDouble() * 2 * Math.PI) - Math.PI;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int targetX = centerX + dx;
                    int targetY = centerY + dy;

                    if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance <= radius && hillContext.canGenerateMoreTiles(totalTiles)) {

                            double tileAngle = Math.atan2(dy, dx);

                            double angleDiff = Math.abs(tileAngle - entranceAngle);
                            if (angleDiff > Math.PI) {
                                angleDiff = (2 * Math.PI) - angleDiff;
                            }

                            int targetElevation;
                            TerrainType targetTerrain;
                            boolean forceNonWalkable = false;

                            if (angleDiff < settings.entranceWidth()) {
                                targetElevation = 1;
                                targetTerrain = TerrainType.HILL;
                            } else {
                                targetElevation = 1;
                                targetTerrain = TerrainType.HILL;
                                forceNonWalkable = true;
                            }

                            TileSnapshot currentTile = context.getTileSnapshot(targetX, targetY);

                            if (currentTile.elevation() <= targetElevation) {
                                Coordinates coords = new Coordinates(targetX, targetY);
                                TerrainModification mod = new TerrainModification(
                                        coords,
                                        targetTerrain,
                                        targetElevation,
                                        null,
                                        forceNonWalkable
                                );

                                modifications.add(mod);
                                hillContext.registerHillTile();
                            }
                        }
                    }
                }
            }
        }
        return modifications;
    }
}
