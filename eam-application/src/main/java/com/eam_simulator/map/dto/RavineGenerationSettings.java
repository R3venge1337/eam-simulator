package com.eam_simulator.map.dto;

public record RavineGenerationSettings(boolean enableRavines,
                                       int numberOfRavines,
                                       int maxBridges,
                                       int ravineLength,
                                       int ravineWidth)
{
}
