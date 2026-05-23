package com.eam_simulator.map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MapBeanConfiguration {

    @Bean
    MapFacade mapFacade(
            GameMapRepository gameMapRepository,
            ApplicationEventPublisher springEventPublisher
    ) {
        return new InitialBootstrapMapService(gameMapRepository, springEventPublisher);
    }
}