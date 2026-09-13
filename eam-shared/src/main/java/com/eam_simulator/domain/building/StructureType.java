package com.eam_simulator.domain.building;

import com.eam_simulator.domain.map.entities.Dimensions;
import com.eam_simulator.domain.map.entities.Offset;

public enum StructureType {
    NONE(null, null),
    STOREHOUSE(new Dimensions(3, 3), new Offset(1, 3));

    private final Dimensions dimensions;
    private final Offset entranceOffset;

    StructureType(Dimensions dimensions, Offset entranceOffset) {
        this.dimensions = dimensions;
        this.entranceOffset = entranceOffset;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public Offset getEntranceOffset() {
        return entranceOffset;
    }
}
