package com.eam_simulator.domain.map.entities;

import java.io.Serializable;

public record Dimensions(int width, int height) implements Serializable {
    public Dimensions {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Wymiary muszą być dodatnie");
        }
    }
}
