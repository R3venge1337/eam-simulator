package com.eam_simulator.map.dto;


public record GenerationSettings(
        HillGenerationSettings hillSettings,
        LakesGenerationSettings lakesSettings,
        MountainGenerationSettings mountainSettings,
        SandGenerationSettings sandSettings
) {
}

