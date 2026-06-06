package com.eam_simulator.map.dto;

public record MountainGenerationSettings(boolean enableMountains,
                                         double maxMountainPercentage,
                                         int numberOfRanges,
                                         int minRidgeLength,
                                         int maxRidgeLength,
                                         int baseRadius,
                                         double passChance,
                                         RavineGenerationSettings ravineSettings
)
{
}
