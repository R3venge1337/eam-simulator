package com.eam_simulator.map;

import com.eam_simulator.map.dto.MapTile;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GameMapMapper {
    public GameMapJpaEntity toJpaEntity(GameMap domain) {
        if(Objects.isNull(domain)){
            return null;
        }

        GameMapJpaEntity jpaEntity = createGameMapJpaEntity(domain);

        return jpaEntity;
    }

    public GameMap toDomain(GameMapJpaEntity entity) {
        if(Objects.isNull(entity)){
            return null;
        }

        int width = entity.getWidth();
        int height = entity.getHeight();
        Tile[][] domainGrid = new Tile[width][height];
        MapTile[][] dtoGrid = entity.getGrid();

        reconstructMapTiles(width, height, dtoGrid, domainGrid);

        return createGameMap(entity, width, height, domainGrid);
    }

    private static @NonNull GameMapJpaEntity createGameMapJpaEntity(GameMap domain) {
        GameMapJpaEntity jpaEntity = new GameMapJpaEntity();
        jpaEntity.setUuid(domain.getId());
        jpaEntity.setName(domain.getMapName().name());
        jpaEntity.setWidth(domain.getSize().width());
        jpaEntity.setHeight(domain.getSize().height());
        jpaEntity.setGrid(mapToDomainTile(domain.getSize().width(),domain.getSize().height(),domain.getGrid()));
        return jpaEntity;
    }

    private static @NonNull GameMap createGameMap(GameMapJpaEntity entity, int width, int height, Tile[][] domainGrid) {
        return new GameMap(
                entity.getUuid(),
                new MapName(entity.getName()),
                new MapSize(width, height),
                domainGrid
        );
    }

    private static void reconstructMapTiles(int width, int height, MapTile[][] dtoGrid, Tile[][] domainGrid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapTile dto = dtoGrid[x][y];
                domainGrid[x][y] = new Tile(dto.coords());
            }
        }
    }

    private static MapTile[][] mapToDomainTile(int width, int height,Tile[][] mapTiles){
        MapTile[][] tiles = new MapTile[width][height];
        for (int i=0; i < width; i++ ){
            for(int y=0; y < height;y++){
                tiles[i][y] = new MapTile(mapTiles[i][y].getCoords());
            }
        }
        return tiles;
    }
}
