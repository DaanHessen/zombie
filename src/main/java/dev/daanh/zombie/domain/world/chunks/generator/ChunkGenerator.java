package dev.daanh.zombie.domain.world.chunks.generator;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ChunkGenerator {
    public static final int GENERATOR_VERSION = 1;

    public Chunk generate(int x, int z, World world) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Chunk generation");

        ChunkCoordinates coordinates = new ChunkCoordinates(x, z);

        log.info("Generating chunk at coordinates {}", coordinates);
        stopWatch.stop();

        Chunk chunk = new Chunk(ChunkState.ACTIVE, coordinates, null, LocalDateTime.now(), GENERATOR_VERSION, stopWatch.getTotalTimeMillis());
        chunk.setWorld(world);

        log.info("Chunk generation completed in {} ms", stopWatch.getTotalTimeMillis());
        return chunk;
    }
}