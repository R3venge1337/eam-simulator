package com.eam_simulator.map.dto;

import com.eam_simulator.engine.entities.Coordinates;

import java.io.Serializable;

public record MapTile(Coordinates coords) implements Serializable {
    public MapTile(Coordinates coords){
        this.coords = coords;
    }
}
