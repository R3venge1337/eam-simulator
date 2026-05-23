package com.eam_simulator.map;

import com.eam_simulator.map.dto.CreateMapCommand;
import com.eam_simulator.map.dto.MapDimension;
import com.eam_simulator.map.dto.MapView;
import com.eam_simulator.map.dto.TileView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class InitialBootstrapMapService implements MapFacade {

    private final GameMapRepository gameMapRepository;
    private final ApplicationEventPublisher springEventPublisher;

    @Override
    @Transactional
    public MapView generateInitialWorld(CreateMapCommand command) {
        GameMap gameMap = new GameMap(
                new MapName(command.mapName()),
                new MapSize(command.size().width(),command.size().height())
        );
        GameMap savedMap = gameMapRepository.save(gameMap);
        gameMap.pullEvents().forEach(springEventPublisher::publishEvent);

        int width = savedMap.getSize().width();
        int height = savedMap.getSize().height();
        TileView[][] viewGrid = new TileView[width][height];
        mapDomainTilesToView(width, height, savedMap.getGrid(), viewGrid);

        return new MapView(
                savedMap.getMapName().name(),
                new MapDimension(command.size().width(),command.size().height()),
                viewGrid
        );
    }

    private void mapDomainTilesToView(int width, int height, Tile[][] domainGrid, TileView[][] viewGrid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile domainTile = domainGrid[x][y];
                viewGrid[x][y] = new TileView(domainTile.getCoords());
            }
        }
    }
}
