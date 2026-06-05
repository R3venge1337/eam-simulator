package com.eam_simulator.map.dto;

public record LakesGenerationSettings(boolean enableLakes,
                                      int numberOfLakes,       // Ile rzek na mapie (np. 1 lub 2)
                                      int minLakeLength,       // Chcesz rzekę na 30 kafelków? Tu wpisujesz 30
                                      int maxLakeLength,       // Maksymalna długość (np. 60)
                                      int lakeWidth,           // Szerokość głębokiej wody (np. 1 lub 2 kafelki)
                                      boolean generateBanks) {
}
