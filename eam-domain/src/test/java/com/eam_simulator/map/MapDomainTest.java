package com.eam_simulator.map;

import com.eam_simulator.engine.event.MapCreatedEvent;
import com.eam_simulator.engine.exceptions.EmptyFieldException;
import com.eam_simulator.engine.exceptions.InvalidMapSizeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapDomainTest {

    @Nested
    @DisplayName("Value Objects invariant validation")
    class ValueObjectsValidation {

        @Test
        @DisplayName("Should throw exception when map name is empty or null")
        void shouldThrowExceptionWhenMapNameIsInvalid() {
            assertThatThrownBy(() -> new MapName(""))
                    .isInstanceOf(EmptyFieldException.class);

            assertThatThrownBy(() -> new MapName(null))
                    .isInstanceOf(EmptyFieldException.class);
        }

        @ParameterizedTest
        @CsvSource({
                "0, 10",
                "10, 0",
                "-5, 10",
                "10, -5"
        })
        @DisplayName("Should throw exception when map dimensions are less than or equal to 0")
        void shouldThrowExceptionWhenDimensionsAreInvalid(int width, int height) {
            assertThatThrownBy(() -> new MapSize(width, height))
                    .isInstanceOf(InvalidMapSizeException.class);
        }

        @Test
        @DisplayName("Should correctly calculate total tiles count")
        void shouldCalculateTotalTiles() {
            MapSize size = new MapSize(10, 5);
            assertThat(size.totalTiles()).isEqualTo(50);
        }

        @ParameterizedTest
        @CsvSource({
                "0, 0, true",
                "9, 4, true",
                "10, 2, false",
                "2, 5, false",
                "-1, 2, false"
        })
        @DisplayName("Should correctly verify if coordinates are within 10x5 map bounds")
        void shouldVerifyBoundsCorrectly(int x, int y, boolean expectedResult) {
            MapSize size = new MapSize(10, 5);
            assertThat(size.isWithinBounds(x, y)).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("GameMap aggregate root business logic")
    class GameMapLogic {

        @Test
        @DisplayName("Should successfully initialize map, fill grid with tiles and register domain event")
        void shouldSuccessfullyCreateMap() {
            // given
            MapName name = new MapName("Test Map");
            MapSize size = new MapSize(3, 3);

            // when
            GameMap gameMap = new GameMap(name, size);

            // then
            assertThat(gameMap.getId()).isNotNull();
            assertThat(gameMap.getMapName().name()).isEqualTo("Test Map");
            assertThat(gameMap.getSize().width()).isEqualTo(3);

            // Verify domain event registration (pullEvents inherited from BaseAggregateRoot)
            assertThat(gameMap.pullEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(MapCreatedEvent.class)
                    .satisfies(event -> {
                        MapCreatedEvent mapEvent = (MapCreatedEvent) event;
                        assertThat(mapEvent.mapId()).isEqualTo(gameMap.getId());
                        assertThat(mapEvent.mapName()).isEqualTo("Test Map");
                        assertThat(mapEvent.width()).isEqualTo(3);
                        assertThat(mapEvent.height()).isEqualTo(3);
                    });
        }
    }
}