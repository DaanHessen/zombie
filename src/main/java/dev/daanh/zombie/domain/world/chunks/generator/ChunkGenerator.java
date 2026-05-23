package dev.daanh.zombie.domain.world.chunks.generator;

import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ChunkGenerator {
    public static final int GENERATOR_VERSION = 1;

    public void generate(int x, int z) {
        ChunkCoordinates coordinates = new ChunkCoordinates(x, z);

        log.info("Generating chunk at coordinates {}", coordinates);

        Chunk chunk = new Chunk(ChunkState.ACTIVE, coordinates, null, LocalDateTime.now(), GENERATOR_VERSION);
    }
}