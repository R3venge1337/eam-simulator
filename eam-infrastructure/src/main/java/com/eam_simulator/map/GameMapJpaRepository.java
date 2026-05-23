package com.eam_simulator.map;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface GameMapJpaRepository extends JpaRepository<GameMapJpaEntity, UUID> {
}
