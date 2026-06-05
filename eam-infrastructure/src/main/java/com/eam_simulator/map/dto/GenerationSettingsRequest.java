package com.eam_simulator.map.dto;

public record GenerationSettingsRequest(HillGenerationSettings hillSettings, LakesGenerationSettings lakesSettings,
                                        MountainGenerationSettings mountainsSettings, SandGenerationSettings sandSettings) {
}
