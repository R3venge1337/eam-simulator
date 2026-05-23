package com.eam_simulator.engine.entities;

import com.eam_simulator.engine.exceptions.InvalidCoordinatesException;

import java.io.Serializable;

public record Coordinates(int x, int y) implements Serializable {

    public Coordinates {
        if (x < 0 || y < 0) {
            throw new InvalidCoordinatesException(x, y);
        }
    }
}
