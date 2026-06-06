package com.eam_simulator.map;

import com.eam_simulator.domain.map.event.MapCreatedEvent;
import com.eam_simulator.map.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialBootstrapMapServiceTest {

    @Mock
    private GameMapRepository gameMapRepository;

    @Mock
    private ApplicationEventPublisher springEventPublisher;

    @Mock
    private MapEnvironmentService environmentService;

    private final List<MapGenerationStep> pipeline = new ArrayList<>();

    private InitialBootstrapMapService mapService;

    @BeforeEach
    void setUp() {
        mapService = new InitialBootstrapMapService(
                gameMapRepository,
                springEventPublisher,
                environmentService,
                pipeline
        );
    }

    @Test
    @DisplayName("Should successfully orchestrate initial map generation, persistence, event publishing and mapping to view")
    void shouldSuccessfullyGenerateMapWorld() {
        //ARRANGE
        HillGenerationSettings hillSettings = new HillGenerationSettings(true, 20, 10, 20, 4);
        LakesGenerationSettings lakesSettings = new LakesGenerationSettings(true, 3,5,6,5,true);
        MountainGenerationSettings mountainSettings = new MountainGenerationSettings(true, 2, 15,15,30,7,0.30, new RavineGenerationSettings(true,2,1,4,5));
        SandGenerationSettings sandSettings = new SandGenerationSettings(true, 5);

        GenerationSettings settings = new GenerationSettings(hillSettings, lakesSettings, mountainSettings, sandSettings);
        MapDimension sizeCommand = new MapDimension(10, 10); // Przykładowy rozmiar
        CreateMapCommand command = new CreateMapCommand("Test World", sizeCommand, settings);

        GameMap stubbedSavedMap = new GameMap(new MapName("Test World"), new MapSize(10, 10));

        when(gameMapRepository.save(any(GameMap.class))).thenReturn(stubbedSavedMap);

        //ACT
        MapView resultView = mapService.generateInitialWorld(command);

        // ASSERT
        ArgumentCaptor<List<Object>> configsCaptor = ArgumentCaptor.forClass(List.class);
        verify(environmentService, times(1)).generate(any(GameMap.class), eq(pipeline), configsCaptor.capture());

        List<Object> capturedConfigs = configsCaptor.getValue();
        assertTrue(capturedConfigs.stream().anyMatch(c -> c instanceof HillGenerationContext));
        assertTrue(capturedConfigs.stream().anyMatch(c -> c instanceof LakesGenerationContext));
        assertTrue(capturedConfigs.stream().anyMatch(c -> c instanceof MountainGenerationContext));
        assertTrue(capturedConfigs.stream().anyMatch(c -> c instanceof SandGenerationContext));

        verify(gameMapRepository, times(1)).save(any(GameMap.class));

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(springEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        Object publishedEvent = eventCaptor.getValue();
        assertInstanceOf(MapCreatedEvent.class, publishedEvent, "Published event should be of type MapCreatedEvent");
        MapCreatedEvent mapEvent = (MapCreatedEvent) publishedEvent;

        assertEquals("Test World", mapEvent.mapName());
        assertEquals(10, mapEvent.width());
        assertEquals(10, mapEvent.height());
        assertNotNull(mapEvent.mapId());
        assertNotNull(mapEvent.occurredOn());

        assertNotNull(resultView);
        assertEquals("Test World", resultView.mapName());
        assertEquals(10, resultView.size().width());
        assertEquals(10, resultView.size().height());
        assertNotNull(resultView.tiles());
        assertEquals(10, resultView.tiles().length);
        assertEquals(10, resultView.tiles()[0].length);
    }
}
