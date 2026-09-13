package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.SandGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Order(3)
class LakeBanksGeneratorStep implements MapGenerationStep {
    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        Optional<SandGenerationSettings> sandSettingsOpt = context.get(SandGenerationSettings.class);
        if (sandSettingsOpt.isEmpty()) {
            return modifications;
        }
        SandGenerationSettings sandSettings = sandSettingsOpt.get();

        if (!sandSettings.enableBeach()) {
            return modifications;
        }

        Optional<SandGenerationContext> sandCtxOpt = context.get(SandGenerationContext.class);
        if (sandCtxOpt.isEmpty()) {
            return modifications;
        }
        SandGenerationContext sandContext = sandCtxOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int beachRadius = sandSettings.beachRadius();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                TileSnapshot currentTile = context.getTileSnapshot(x, y);

                // OCHRONA: Plaże stawiamy wyłącznie na czystej trawie (GRASS) na niskim poziomie
                if (currentTile.terrain() != TerrainType.GRASS || currentTile.elevation() > 1) {
                    continue;
                }

                if (isNearWater(x, y, beachRadius, context)) {
                    Coordinates coords = new Coordinates(x, y);

                    modifications.add(new TerrainModification(coords, TerrainType.SAND, 0, PassageType.FREE, true));
                    sandContext.registerSandTile();
                }
            }
        }
        return modifications;
    }

    private boolean isNearWater(int centerX, int centerY, int radius, MapGenerationContext context) {
        int width = context.getWidth();
        int height = context.getHeight();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx == 0 && dy == 0) continue;

                int targetX = centerX + dx;
                int targetY = centerY + dy;

                if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
                    TileSnapshot tile = context.getTileSnapshot(targetX, targetY);
                    if (tile.terrain() == TerrainType.WATER || tile.terrain() == TerrainType.SHALLOW_WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
