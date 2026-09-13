package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.HillGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(4)
class HillGeneratorStep implements MapGenerationStep {
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        Optional<HillGenerationContext> hillCtxOpt = context.get(HillGenerationContext.class);
        Optional<HillGenerationSettings> settingsOpt = context.get(HillGenerationSettings.class);

        if (hillCtxOpt.isEmpty() || settingsOpt.isEmpty() || !settingsOpt.get().enableHills()) {
            return modifications;
        }

        HillGenerationContext hillContext = hillCtxOpt.get();
        HillGenerationSettings settings = settingsOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        Set<Coordinates> generatedHillTiles = new HashSet<>();

        int maxAttempts = 200;
        int attempts = 0;

        while (hillContext.canGenerateMoreTiles(totalTiles) && attempts < maxAttempts) {
            attempts++;

            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);

            int minR = settings.minHillRadius();
            int maxR = settings.maxHillRadius();
            int radiusX = random.nextInt(maxR - minR + 1) + minR;
            int radiusY = random.nextInt(maxR - minR + 1) + minR;

            double entranceAngle = (random.nextDouble() * 2 * Math.PI) - Math.PI;
            double entranceWidth = settings.entranceWidth();

            // Próg wewnętrzny określający grubość ścianki zewnętrznej (ok. 1.2 kafelka od krawędzi)
            double minRadius = Math.min(radiusX, radiusY);
            double innerRatioThreshold = Math.max(0.0, (minRadius - 1.2) / minRadius);

            boolean addedAnyNewTile = false;

            for (int dx = -radiusX; dx <= radiusX; dx++) {
                for (int dy = -radiusY; dy <= radiusY; dy++) {
                    int targetX = centerX + dx;
                    int targetY = centerY + dy;

                    if (isOutOfBounds(targetX, targetY, width, height)) {
                        continue;
                    }

                    double ellipseDistance = Math.pow((double) dx / radiusX, 2) + Math.pow((double) dy / radiusY, 2);

                    if (ellipseDistance <= 1.0) {
                        Coordinates coords = new Coordinates(targetX, targetY);

                        if (generatedHillTiles.contains(coords)) {
                            continue;
                        }

                        // Kąt kafelka względem środka wzgórza
                        double tileAngle = Math.atan2(dy, dx);
                        double angleDiff = Math.abs(tileAngle - entranceAngle);
                        if (angleDiff > Math.PI) {
                            angleDiff = (2 * Math.PI) - angleDiff;
                        }

                        // Znormalizowany promień (0.0 = środek, 1.0 = zewnętrzny brzeg elipsy)
                        double normRadius = Math.sqrt(ellipseDistance);

                        // Krawędź zewnętrzna (klif)
                        boolean isOuterEdge = normRadius >= innerRatioThreshold;

                        // Wejście/Rampa na krawędzi
                        boolean isEntrance = isOuterEdge && (angleDiff <= (entranceWidth / 2.0));

                        // Blokujemy TYLKO zewnętrzną krawędź, chyba że jest to wejście
                        boolean isCliff = isOuterEdge && !isEntrance;

                        PassageType passageType = isCliff ? PassageType.BLOCKED : PassageType.FREE;
                        boolean isWalkable = !isCliff;

                        TileSnapshot currentTile = context.getTileSnapshot(targetX, targetY);

                        if (currentTile.terrain() != TerrainType.GRASS) {
                            continue;
                        }

                        if (currentTile.elevation() == 0) {
                            int targetElevation = 1;

                            modifications.add(new TerrainModification(
                                    coords,
                                    TerrainType.HILL,
                                    targetElevation,
                                    passageType,
                                    isWalkable
                            ));

                            generatedHillTiles.add(coords);
                            hillContext.registerHillTile();
                            addedAnyNewTile = true;

                            if (!hillContext.canGenerateMoreTiles(totalTiles)) {
                                break;
                            }
                        }
                    }
                }
                if (!hillContext.canGenerateMoreTiles(totalTiles)) {
                    break;
                }
            }

            if (addedAnyNewTile) {
                attempts = 0;
            }
        }

        return modifications;
    }

    private boolean isOutOfBounds(int x, int y, int width, int height) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }
}
