package com.eam_simulator.map.generator;

import com.eam_simulator.domain.building.StructureType;
import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.ResourceType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.domain.unit.UnitOffset;
import com.eam_simulator.domain.unit.UnitType;
import com.eam_simulator.map.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Order(5)
class FactionBaseGeneratorStep implements MapGenerationStep {
    private static final int BASE_WIDTH = 3;
    private static final int BASE_HEIGHT = 3;
    private static final int BUILD_ZONE_CLEAR_RADIUS = 6;

    private static final List<UnitOffset> STARTING_UNITS_CONFIG = List.of(
            new UnitOffset(0, 3, UnitType.BUILDER),
            new UnitOffset(1, 3, UnitType.BUILDER),
            new UnitOffset(2, 3, UnitType.BUILDER),

            new UnitOffset(-1, 4, UnitType.SERF),
            new UnitOffset(0,  4, UnitType.SERF),
            new UnitOffset(1,  4, UnitType.SERF),
            new UnitOffset(2,  4, UnitType.SERF)
    );

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        int width = context.getWidth();
        int height = context.getHeight();

        FactionGenerationContext factionContext = context.get(FactionGenerationContext.class)
                .orElseThrow(() -> new IllegalStateException("FactionGenerationContext is missing from MapGenerationContext!"));

        int totalPlayers = factionContext.getTotalPlayers();
        validatePlayerLimit(width, height, totalPlayers);

        System.out.println("--- ROZPOCZĘCIE GENEROWANIA BAZ (Gracze: " + totalPlayers + ") ---");

        // 1. Spróbuj znaleźć idealną, obróconą dla wszystkich graczy symetryczną siatkę
        List<Coordinates> baseTopLeftPoints = findSymmetricalBaseLocations(context, totalPlayers);

        // 2. Jeśli symetria obrotowa się nie powiodła, wylicz bazowe i znajdź lokalne zastępstwa
        if (baseTopLeftPoints == null) {
            System.out.println("Brak możliwości zachowania idealnej symetrii. Uruchamianie algorytmu lokalnego (fallback)...");
            List<Coordinates> idealCenters = calculateRadialStartingPoints(width, height, totalPlayers, 0.0);
            baseTopLeftPoints = new ArrayList<>();

            for (Coordinates idealCenter : idealCenters) {
                baseTopLeftPoints.add(findNearestSafeAreaForBase(idealCenter, context));
            }
        }

        // 3. Stwórz bazy, modyfikacje terenu i jednostki dla każdego gracza
        for (int i = 0; i < totalPlayers; i++) {
            int ownerId = i + 1;
            Coordinates baseTopLeft = baseTopLeftPoints.get(i);

            System.out.println("Gracz #" + ownerId + " -> Wybrany lewy-górny róg bazy 3x3: ("
                    + baseTopLeft.x() + ", " + baseTopLeft.y() + ")");

            // A. Czyszczenie i wyrównywanie większej strefy wokół bazy pod budowę pierwszych budynków
            clearBuildZoneAroundBase(baseTopLeft, width, height, modifications);

            // B. Modyfikacja terenu bezpośrednio pod Spichlerz (3x3): Blokada i poziom 0
            for (int dx = 0; dx < BASE_WIDTH; dx++) {
                for (int dy = 0; dy < BASE_HEIGHT; dy++) {
                    Coordinates tileCoords = new Coordinates(baseTopLeft.x() + dx, baseTopLeft.y() + dy);

                    modifications.add(new TerrainModification(
                            tileCoords,
                            TerrainType.GRASS,
                            0,
                            PassageType.BLOCKED,
                            false
                    ));
                }
            }

            // C. Rejestracja Spichlerza w FactionGenerationContext
            Map<ResourceType, Integer> startingStock = Arrays.stream(ResourceType.values())
                    .collect(Collectors.toMap(
                            resourceType -> resourceType,
                            resourceType -> ThreadLocalRandom.current().nextInt(0, 1000) // 1000 jest ekskluzywne, więc wylosuje max 999
                    ));

            factionContext.registerStructure(ownerId, new StartingStructureData(
                    baseTopLeft,
                    StructureType.STOREHOUSE,
                    ownerId,
                    550,
                    550,
                    startingStock
            ));

            for (UnitOffset unitConfig : STARTING_UNITS_CONFIG) {
                Coordinates unitCoords = new Coordinates(
                        baseTopLeft.x() + unitConfig.dx(),
                        baseTopLeft.y() + unitConfig.dy()
                );

                // Wyrównanie i udrożnienie terenu pod konkretną jednostkę
                prepareTileForUnitSpawn(unitCoords, modifications);

                // Rejestracja jednostki dla danego gracza
                factionContext.registerUnit(ownerId, new StartingUnitData(unitCoords, unitConfig.type(), ownerId));
            }
        }

        return modifications;
    }

    /**
     * Wyrównuje teren i usuwa ewentualne przeszkody w promieniu wokół bazy gracza.
     */
    private void clearBuildZoneAroundBase(Coordinates topLeft, int mapW, int mapH, List<TerrainModification> modifications) {
        int centerX = topLeft.x() + 1;
        int centerY = topLeft.y() + 1;

        for (int dx = -BUILD_ZONE_CLEAR_RADIUS; dx <= BUILD_ZONE_CLEAR_RADIUS; dx++) {
            for (int dy = -BUILD_ZONE_CLEAR_RADIUS; dy <= BUILD_ZONE_CLEAR_RADIUS; dy++) {
                int targetX = centerX + dx;
                int targetY = centerY + dy;

                if (targetX >= 0 && targetX < mapW && targetY >= 0 && targetY < mapH) {
                    Coordinates coords = new Coordinates(targetX, targetY);
                    modifications.add(new TerrainModification(
                            coords,
                            TerrainType.GRASS,
                            0,
                            PassageType.FREE,
                            true
                    ));
                }
            }
        }
    }

    private List<Coordinates> findSymmetricalBaseLocations(MapGenerationContext context, int totalPlayers) {
        int width = context.getWidth();
        int height = context.getHeight();

        for (int step = 0; step <= 9; step++) {
            int angleDeg = (step % 2 == 0) ? (step / 2) * 5 : -(step / 2 + 1) * 5;
            double angleOffset = Math.toRadians(angleDeg);

            List<Coordinates> candidateCenters = calculateRadialStartingPoints(width, height, totalPlayers, angleOffset);
            List<Coordinates> candidateTopLefts = candidateCenters.stream()
                    .map(center -> new Coordinates(center.x() - 1, center.y() - 1))
                    .toList();

            boolean allValid = candidateTopLefts.stream()
                    .allMatch(topLeft -> isAreaSuitableForBase(topLeft, context));

            if (allValid) {
                System.out.println("Znalazłem IDEALNĄ symetrię obrotową z przesunięciem kątowym: " + angleDeg + "°");
                return candidateTopLefts;
            }
        }
        return null;
    }

    private List<Coordinates> calculateRadialStartingPoints(int w, int h, int players, double angleOffset) {
        List<Coordinates> points = new ArrayList<>();
        int centerX = w / 2;
        int centerY = h / 2;

        double radiusX = w * 0.35;
        double radiusY = h * 0.35;
        double angleStep = 2 * Math.PI / players;

        for (int i = 0; i < players; i++) {
            double angle = (i * angleStep) + angleOffset;
            int targetX = (int) Math.round(centerX + radiusX * Math.cos(angle));
            int targetY = (int) Math.round(centerY + radiusY * Math.sin(angle));

            targetX = Math.clamp(targetX, 12, w - 12);
            targetY = Math.clamp(targetY, 12, h - 12);

            points.add(new Coordinates(targetX, targetY));
        }
        return points;
    }

    private Coordinates findNearestSafeAreaForBase(Coordinates start, MapGenerationContext context) {
        Coordinates startTopLeft = new Coordinates(start.x() - 1, start.y() - 1);
        if (isAreaSuitableForBase(startTopLeft, context)) {
            return startTopLeft;
        }

        for (int radius = 1; radius < 30; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (Math.abs(dx) == radius || Math.abs(dy) == radius) {
                        int checkX = startTopLeft.x() + dx;
                        int checkY = startTopLeft.y() + dy;

                        if (checkX >= 10 && checkX < context.getWidth() - 10 && checkY >= 10 && checkY < context.getHeight() - 10) {
                            Coordinates candidate = new Coordinates(checkX, checkY);
                            if (isAreaSuitableForBase(candidate, context)) {
                                return candidate;
                            }
                        }
                    }
                }
            }
        }
        return startTopLeft;
    }

    private boolean isAreaSuitableForBase(Coordinates topLeft, MapGenerationContext context) {
        int margin = 1;

        for (int dx = -margin; dx < BASE_WIDTH + margin; dx++) {
            for (int dy = -margin; dy < BASE_HEIGHT + margin; dy++) {
                int checkX = topLeft.x() + dx;
                int checkY = topLeft.y() + dy;

                if (checkX < 0 || checkX >= context.getWidth() || checkY < 0 || checkY >= context.getHeight()) {
                    return false;
                }

                var snapshot = context.getTileSnapshot(checkX, checkY);

                boolean isFlat = snapshot.elevation() == 0;
                boolean isBuildableTerrain = snapshot.terrain() == TerrainType.GRASS || snapshot.terrain() == TerrainType.SAND;
                boolean isWalkable = snapshot.isWalkable() && snapshot.passageType() == PassageType.FREE;

                if (!isFlat || !isBuildableTerrain || !isWalkable) {
                    return false;
                }
            }
        }
        return true;
    }

    private void prepareTileForUnitSpawn(Coordinates coords, List<TerrainModification> modifications) {
        modifications.add(new TerrainModification(
                coords,
                TerrainType.GRASS,
                0,
                PassageType.FREE,
                true
        ));
    }

    private void validatePlayerLimit(int width, int height, int totalPlayers) {
        if (width <= 128 && height <= 128 && totalPlayers > 4) {
            throw new IllegalArgumentException("Map 128x128 is too small for " + totalPlayers + " players. Max limit is 4.");
        }
        if (totalPlayers > 8) {
            throw new IllegalArgumentException("This algorithm supports a maximum of 8 players.");
        }
    }
}