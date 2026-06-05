package com.eam_simulator.map;

import com.eam_simulator.map.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
class InitialBootstrapMapService implements MapFacade {

    private final GameMapRepository gameMapRepository;
    private final ApplicationEventPublisher springEventPublisher;
    private final MapEnvironmentService environmentService;
    private final List<MapGenerationStep> pipeline;

    @Override
    @Transactional
    public MapView generateInitialWorld(CreateMapCommand command) {
        GameMap gameMap = new GameMap(
                new MapName(command.mapName()),
                new MapSize(command.size().width(), command.size().height())
        );

        List<Object> activeConfigs = new ArrayList<>();
        GenerationSettings settings = command.settings();

        activeConfigs.add(settings.hillSettings());
        activeConfigs.add(settings.lakesSettings());
        activeConfigs.add(settings.mountainSettings());
        activeConfigs.add(settings.sandSettings());


        if (settings.hillSettings().enableHills()) {
            activeConfigs.add(new HillGenerationContext(true, settings.hillSettings().maxHillPercentage()));
        }

        if (settings.lakesSettings().enableLakes()) {
            activeConfigs.add(new LakesGenerationContext(true, settings.lakesSettings().numberOfLakes(), 0.30));
        }

        if (settings.mountainSettings().enableMountains()) {
            activeConfigs.add(new MountainGenerationContext(true, settings.mountainSettings().numberOfRanges(), settings.mountainSettings().maxMountainPercentage()));
        }

        if (settings.sandSettings().enableBeach()) {
            activeConfigs.add(new SandGenerationContext());
        }

        environmentService.generate(gameMap, this.pipeline, activeConfigs);

        GameMap savedMap = gameMapRepository.save(gameMap);

        gameMap.pullEvents().forEach(springEventPublisher::publishEvent);

        int width = savedMap.getSize().width();
        int height = savedMap.getSize().height();
        TileView[][] viewGrid = new TileView[width][height];
        mapDomainTilesToView(width, height, savedMap.getGrid(), viewGrid);

        return new MapView(
                savedMap.getMapName().name(),
                new MapDimension(command.size().width(), command.size().height()),
                viewGrid
        );
    }

    private void mapDomainTilesToView(int width, int height, Tile[][] domainGrid, TileView[][] viewGrid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile domainTile = domainGrid[x][y];
                viewGrid[x][y] = new TileView(domainTile.getCoords(), domainTile.getTerrain(), domainTile.getElevation(), domainTile.getPassage(), domainTile.isWalkable());
            }
        }
    }
}
