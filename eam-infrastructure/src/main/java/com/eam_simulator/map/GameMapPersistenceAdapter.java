package com.eam_simulator.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class GameMapPersistenceAdapter implements GameMapRepository {

    private final GameMapJpaRepository gameMapJpaRepository;
    private final GameMapMapper mapMapper;

    @Override
    public GameMap save(GameMap gameMap) {
        GameMapJpaEntity jpaEntity = mapMapper.toJpaEntity(gameMap);
        GameMapJpaEntity savedEntity = gameMapJpaRepository.save(jpaEntity);
        return mapMapper.toDomain(savedEntity);
    }
}
