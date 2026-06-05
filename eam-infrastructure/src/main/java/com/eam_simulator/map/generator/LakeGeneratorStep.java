package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.LakesGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@Order(2)
class LakeGeneratorStep implements MapGenerationStep {
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        Optional<LakesGenerationSettings> settingsOpt = context.get(LakesGenerationSettings.class);
        if (settingsOpt.isEmpty() || !settingsOpt.get().enableLakes()) {
            return modifications;
        }
        LakesGenerationSettings settings = settingsOpt.get();

        Optional<LakesGenerationContext> lakesCtxOpt = context.get(LakesGenerationContext.class);
        if (lakesCtxOpt.isEmpty()) {
            return modifications;
        }
        LakesGenerationContext lakesContext = lakesCtxOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        while (lakesContext.shouldGenerateNextRiver()) {

            if (!lakesContext.canGenerateMoreTiles(totalTiles)) {
                break;
            }

            int targetLength = random.nextInt(settings.maxLakeLength() - settings.minLakeLength() + 1) + settings.minLakeLength();
            int curX = random.nextInt(width);
            int curY = 0;

            int mainDirX = random.nextBoolean() ? 1 : -1;
            int mainDirY = 1;

            int currentLength = 0;

            while (currentLength < targetLength) {
                if (curX < 0 || curX >= width || curY < 0 || curY >= height) {
                    break;
                }

                if (!lakesContext.canGenerateMoreTiles(totalTiles)) {
                    break;
                }

                int halfWidth = settings.lakeWidth() / 2;
                for (int w = -halfWidth; w <= halfWidth; w++) {
                    int waterX = curX + w;

                    if (waterX >= 0 && waterX < width) {
                        Coordinates coords = new Coordinates(waterX, curY);
                        TileSnapshot currentTile = context.getTileSnapshot(waterX, curY);

                        if (currentTile.elevation() < 2) {

                            modifications.add(new TerrainModification(coords, TerrainType.WATER, 0,null,false));
                            lakesContext.registerWaterTile();

                            if (settings.generateBanks()) {
                                generateRiverBanks(waterX, curY, context, modifications, lakesContext, totalTiles);
                            }
                        }
                    }
                }

                if (random.nextDouble() < 0.7) {
                    curY += mainDirY;
                    if (random.nextDouble() < 0.3) {
                        curX += mainDirX;
                    }
                } else {
                    curX += (random.nextBoolean() ? 2 : -2);
                }

                currentLength++;
            }

            lakesContext.incrementRiversCount();
        }
        return modifications;
    }

    private void generateRiverBanks(int rx, int ry, MapGenerationContext context,
                                    List<TerrainModification> modifications,
                                    LakesGenerationContext lakesContext, int totalTiles) {
        int width = context.getWidth();

        for (int dx = -1; dx <= 1; dx++) {
            int bx = rx + dx;
            if (bx >= 0 && bx < width) {
                if (!lakesContext.canGenerateMoreTiles(totalTiles)) {
                    return;
                }

                TileSnapshot neighbor = context.getTileSnapshot(bx, ry);

                if (neighbor.terrain() != TerrainType.WATER && neighbor.terrain() != TerrainType.SHALLOW_WATER && neighbor.elevation() < 2) {
                    modifications.add(new TerrainModification(new Coordinates(bx, ry), TerrainType.SHALLOW_WATER, 0,null,true));
                    lakesContext.registerWaterTile();
                }
            }
        }
    }
}
