package com.eam_simulator.map;

import java.util.List;

public class MapEnvironmentService {
    public void generate(GameMap gameMap, List<MapGenerationStep> pipeline, List<Object> activeConfigs) {
        gameMap.startEnvironmentGeneration();

        MapGenerationContext context = new MapGenerationContext(
                gameMap.getSize().width(),
                gameMap.getSize().height()
        );

        for (Object config : activeConfigs) {
            Class<Object> type = (Class<Object>) config.getClass();
            context.register(type, config);
        }

        try {
            for (MapGenerationStep step : pipeline) {

                List<TerrainModification> modifications = step.execute(context);

                for (TerrainModification mod : modifications) {

                    gameMap.applyModification(mod);

                    context.updateSnapshot(
                            mod.coordinates().x(),
                            mod.coordinates().y(),
                            mod.terrainType(),
                            mod.elevation(),
                            mod.passageType(),
                            mod.isWalkable()
                    );
                }
            }
            gameMap.completeEnvironmentGeneration();

        } catch (Exception e) {
            gameMap.failedEnvironmentGeneration();
            throw e;
        }
    }
}
