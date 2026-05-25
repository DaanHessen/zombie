package dev.daanh.zombie.domain.world.chunks.generator;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChunkGenerator {
    public static final int GENERATOR_VERSION = 1;

    private final SettlementRepository settlementRepository;
    private final GameConfig config;

    public Chunk generate(int x, int z, World world) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Chunk generation");

        ChunkCoordinates coordinates = new ChunkCoordinates(x, z);

        log.info("Generating chunk at coordinates {}", coordinates);

        double size = config.getWorld().getChunkSizeKm();
        double minLat = coordinates.getMinCoordinates(size).getLatitude();
        double minLon = coordinates.getMinCoordinates(size).getLongitude();
        double maxLat = coordinates.getMaxCoordinates(size).getLatitude();
        double maxLon = coordinates.getMaxCoordinates(size).getLongitude();

        List<Settlement> settlementsInChunk = settlementRepository.findByCoordinatesLatitudeBetweenAndCoordinatesLongitudeBetween(
                minLat, maxLat, minLon, maxLon
        );

        Settlement settlement = settlementsInChunk.isEmpty() ? null : settlementsInChunk.get(0);

        stopWatch.stop();

        Chunk chunk = new Chunk(ChunkState.ACTIVE, coordinates, settlement, LocalDateTime.now(), GENERATOR_VERSION, stopWatch.getTotalTimeMillis());
        chunk.setWorld(world);

        log.info("Chunk generation completed in {} ms (Found Settlement: {})", stopWatch.getTotalTimeMillis(), settlement != null ? settlement.getName() : "None");
        return chunk;
    }
}