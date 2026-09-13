package com.eam_simulator.map.generator;

import com.eam_simulator.domain.map.entities.Coordinates;
import com.eam_simulator.domain.map.entities.PassageType;
import com.eam_simulator.domain.map.entities.TerrainType;
import com.eam_simulator.map.*;
import com.eam_simulator.map.dto.LakesGenerationSettings;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(2)
class LakeGeneratorStep implements MapGenerationStep {
    private static final int SPAWN_PROTECTION_RADIUS = 14; // Bezpieczny promień wokół baz graczy
    private final Random random = new Random();

    @Override
    public List<TerrainModification> execute(MapGenerationContext context) {
        List<TerrainModification> modifications = new ArrayList<>();

        Optional<LakesGenerationSettings> settingsOpt = context.get(LakesGenerationSettings.class);
        if (settingsOpt.isEmpty() || !settingsOpt.get().enableLakes()) {
            return modifications;
        }
        LakesGenerationSettings settings = settingsOpt.get();

        Optional<LakesGenerationContext> lakesCtxOpt = context.get(LakesGenerationContext.class);
        if (lakesCtxOpt.isEmpty()) {
            return modifications;
        }
        LakesGenerationContext lakesContext = lakesCtxOpt.get();

        int width = context.getWidth();
        int height = context.getHeight();
        int totalTiles = context.getTotalTiles();

        int totalPlayers = context.get(FactionGenerationContext.class)
                .map(FactionGenerationContext::getTotalPlayers)
                .orElse(4);
        List<Coordinates> estimatedSpawns = calculateRadialStartingPoints(width, height, totalPlayers);

        List<RiverSourceInfo> startPoints = selectDistributedStartPoints(context, settings.numberOfLakes());
        int riverIndex = 0;

        Set<Coordinates> deepWaterTiles = new HashSet<>();
        Set<Coordinates> shallowWaterTiles = new HashSet<>();
        Set<Coordinates> bankTiles = new HashSet<>();

        while (lakesContext.shouldGenerateNextRiver()) {

            if (!lakesContext.canGenerateMoreTiles(totalTiles)) {
                break;
            }

            int targetLength = random.nextInt(settings.maxLakeLength() - settings.minLakeLength() + 1) + settings.minLakeLength();

            RiverSourceInfo sourceInfo = (riverIndex < startPoints.size())
                    ? startPoints.get(riverIndex)
                    : new RiverSourceInfo(
                    new Coordinates(random.nextInt(width - 20) + 10, random.nextInt(height - 20) + 10),
                    random.nextDouble() * Math.PI * 2
            );

            riverIndex++;

            double floatX = sourceInfo.coordinates().x();
            double floatY = sourceInfo.coordinates().y();
            double angle = sourceInfo.initialAngle();

            Set<Integer> bridgeSteps = determineBridgeStepIndexes(targetLength);
            int bridgeThickness = 2;

            int currentLength = 0;
            Set<Coordinates> currentRiverDeep = new HashSet<>();

            // A. Wyznaczanie koryta rzeki
            while (currentLength < targetLength) {
                int curX = (int) Math.round(floatX);
                int curY = (int) Math.round(floatY);

                if (curX < 0 || curX >= width || curY < 0 || curY >= height) {
                    break;
                }

                if (!lakesContext.canGenerateMoreTiles(totalTiles)) {
                    break;
                }

                Coordinates centerCoords = new Coordinates(curX, curY);

                // OCHRONA BAZY: Jeśli główny nurt wchodzi w strefę gracza -> Kończymy rzekę w tym miejscu!
                // Zapobiega to przechodzeniu rzeki "przez" bazę i tworzeniu oderwanych wysp wodnych.
                if (isNearPlayerSpawn(centerCoords, estimatedSpawns, SPAWN_PROTECTION_RADIUS)) {
                    break;
                }

                boolean isBridgeStep = isStepInBridgeZone(currentLength, bridgeSteps, bridgeThickness);
                int halfWidth = settings.lakeWidth() / 2;

                for (int wx = -halfWidth; wx <= halfWidth; wx++) {
                    for (int wy = -halfWidth; wy <= halfWidth; wy++) {
                        int waterX = curX + wx;
                        int waterY = curY + wy;

                        if (waterX >= 0 && waterX < width && waterY >= 0 && waterY < height) {
                            Coordinates coords = new Coordinates(waterX, waterY);
                            TileSnapshot currentTile = context.getTileSnapshot(waterX, waterY);

                            if (currentTile.terrain() == TerrainType.MOUNTAIN) {
                                continue;
                            }

                            if (currentTile.terrain() == TerrainType.HILL || isBridgeStep) {
                                shallowWaterTiles.add(coords);
                            } else if (currentTile.terrain() == TerrainType.GRASS && currentTile.elevation() < 2) {
                                currentRiverDeep.add(coords);
                            }
                        }
                    }
                }

                angle += (random.nextDouble() - 0.5) * 0.35;
                floatX += Math.cos(angle) * 1.5;
                floatY += Math.sin(angle) * 1.5;

                currentLength++;
            }

            for (Coordinates waterCoord : currentRiverDeep) {
                if (!shallowWaterTiles.contains(waterCoord)) {
                    deepWaterTiles.add(waterCoord);
                }
            }

            // B. Wyznaczanie brzegów rzeki
            if (settings.generateBanks()) {
                Set<Coordinates> allRiverCenterTiles = new HashSet<>(deepWaterTiles);
                allRiverCenterTiles.addAll(shallowWaterTiles);

                for (Coordinates waterCoord : allRiverCenterTiles) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int bx = waterCoord.x() + dx;
                            int by = waterCoord.y() + dy;

                            if (bx >= 0 && bx < width && by >= 0 && by < height) {
                                Coordinates bankCoord = new Coordinates(bx, by);

                                if (!allRiverCenterTiles.contains(bankCoord)) {
                                    TileSnapshot neighbor = context.getTileSnapshot(bx, by);
                                    if (neighbor.terrain() == TerrainType.GRASS && neighbor.elevation() < 2) {
                                        bankTiles.add(bankCoord);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            lakesContext.incrementRiversCount();
        }

        // C. Budowanie modyfikacji terenu
        for (Coordinates shallowCoord : shallowWaterTiles) {
            lakesContext.registerWaterTile();
            modifications.add(new TerrainModification(shallowCoord, TerrainType.SHALLOW_WATER, 0, PassageType.FREE, true));
        }

        for (Coordinates bankCoord : bankTiles) {
            if (!deepWaterTiles.contains(bankCoord) && !shallowWaterTiles.contains(bankCoord)) {
                lakesContext.registerWaterTile();
                modifications.add(new TerrainModification(bankCoord, TerrainType.SHALLOW_WATER, 0, PassageType.FREE, true));
            }
        }

        for (Coordinates deepCoord : deepWaterTiles) {
            lakesContext.registerWaterTile();
            modifications.add(new TerrainModification(deepCoord, TerrainType.WATER, 0, PassageType.BLOCKED, false));
        }

        return modifications;
    }

    private boolean isNearPlayerSpawn(Coordinates coords, List<Coordinates> spawns, int safeRadius) {
        for (Coordinates spawn : spawns) {
            double distSq = Math.pow(coords.x() - spawn.x(), 2) + Math.pow(coords.y() - spawn.y(), 2);
            if (distSq <= Math.pow(safeRadius, 2)) {
                return true;
            }
        }
        return false;
    }

    private List<Coordinates> calculateRadialStartingPoints(int w, int h, int players) {
        List<Coordinates> points = new ArrayList<>();
        int centerX = w / 2;
        int centerY = h / 2;
        double radiusX = w * 0.35;
        double radiusY = h * 0.35;
        double angleStep = 2 * Math.PI / players;

        for (int i = 0; i < players; i++) {
            double angle = i * angleStep;
            int targetX = (int) Math.round(centerX + radiusX * Math.cos(angle));
            int targetY = (int) Math.round(centerY + radiusY * Math.sin(angle));
            points.add(new Coordinates(targetX, targetY));
        }
        return points;
    }

    private Set<Integer> determineBridgeStepIndexes(int targetLength) {
        Set<Integer> indexes = new HashSet<>();
        if (targetLength < 25) return indexes;
        indexes.add((int) (targetLength * 0.35) + random.nextInt(5) - 2);
        if (targetLength > 50) {
            indexes.add((int) (targetLength * 0.70) + random.nextInt(5) - 2);
        }
        return indexes;
    }

    private boolean isStepInBridgeZone(int currentStep, Set<Integer> bridgeSteps, int thickness) {
        for (int bridgeStep : bridgeSteps) {
            if (Math.abs(currentStep - bridgeStep) <= thickness / 2) return true;
        }
        return false;
    }

    private List<RiverSourceInfo> selectDistributedStartPoints(MapGenerationContext context, int targetCount) {
        List<RiverSourceInfo> points = new ArrayList<>();
        int w = context.getWidth();
        int h = context.getHeight();
        int cols = (int) Math.ceil(Math.sqrt(targetCount));
        int rows = (int) Math.ceil((double) targetCount / cols);
        int sectorW = w / cols;
        int sectorH = h / rows;

        for (int r = 0; r < rows && points.size() < targetCount; r++) {
            for (int c = 0; c < cols && points.size() < targetCount; c++) {
                int minX = c * sectorW + 8;
                int maxX = (c + 1) * sectorW - 8;
                int minY = r * sectorH + 8;
                int maxY = (r + 1) * sectorH - 8;

                RiverSourceInfo source = findMountainEdgeInSector(context, minX, maxX, minY, maxY);

                if (source == null) {
                    int startX = minX + random.nextInt(Math.max(1, maxX - minX));
                    int startY = minY + random.nextInt(Math.max(1, maxY - minY));
                    source = new RiverSourceInfo(new Coordinates(startX, startY), random.nextDouble() * Math.PI * 2);
                }
                points.add(source);
            }
        }
        return points;
    }

    private RiverSourceInfo findMountainEdgeInSector(MapGenerationContext context, int minX, int maxX, int minY, int maxY) {
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                TileSnapshot snapshot = context.getTileSnapshot(x, y);
                if (snapshot.terrain() == TerrainType.MOUNTAIN) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < context.getWidth() && ny >= 0 && ny < context.getHeight()) {
                                if (context.getTileSnapshot(nx, ny).terrain() == TerrainType.GRASS) {
                                    return new RiverSourceInfo(new Coordinates(nx, ny), Math.atan2(dy, dx));
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private record RiverSourceInfo(Coordinates coordinates, double initialAngle) {}
}

