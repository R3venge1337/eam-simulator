package com.eam_simulator.map.dto;

public record HillGenerationSettings(boolean enableHills,
                                     double maxHillPercentage,
                                     int minHillRadius,
                                     int maxHillRadius,
                                     double entranceWidth) {
}
