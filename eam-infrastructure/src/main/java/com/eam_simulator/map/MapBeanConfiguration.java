package com.eam_simulator.map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class MapBeanConfiguration {

    @Bean
    MapEnvironmentService mapEnvironmentService() {
        return new MapEnvironmentService();
    }

    @Bean
    public List<MapGenerationStep> mapGenerationPipeline(List<MapGenerationStep> allSteps) {
        return allSteps;
    }

    @Bean
    MapFacade mapFacade(
            GameMapRepository gameMapRepository,
            ApplicationEventPublisher springEventPublisher,
            MapEnvironmentService environmentService,
            List<MapGenerationStep> pipeline
    ) {
        return new InitialBootstrapMapService(gameMapRepository, springEventPublisher, environmentService, pipeline);
    }
}