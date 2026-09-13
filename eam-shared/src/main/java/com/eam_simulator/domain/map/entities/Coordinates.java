package com.eam_simulator.domain.map.entities;

import com.eam_simulator.domain.map.exceptions.InvalidCoordinatesException;

import java.io.Serializable;

public record Coordinates(int x, int y) implements Serializable {

    public Coordinates {
        if (x < 0 || y < 0) {
            throw new InvalidCoordinatesException(x, y);
        }
    }
    public Coordinates add(Offset offset) {
        return new Coordinates(this.x + offset.dx(), this.y + offset.dy());
    }

    /**
     * Odległość taksówkowa (Manhattan Distance) do celu.
     * Niezbędna dla logistyki tragarzy i szukania dróg A*.
     */
    public int manhattanDistanceTo(Coordinates target) {
        return Math.abs(this.x - target.x) + Math.abs(this.y - target.y);
    }
}
