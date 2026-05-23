package com.eam_simulator.map;

import com.eam_simulator.common.AbstractUUIDEntity;
import com.eam_simulator.map.dto.MapTile;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maps")
@Getter
@Setter
class GameMapJpaEntity extends AbstractUUIDEntity {
    private String name;
    private int width;
    private int height;

    @Convert(converter = TileGridConverter.class)
    @Column(name = "grid", columnDefinition = "TEXT")
    private MapTile[][] grid;
}