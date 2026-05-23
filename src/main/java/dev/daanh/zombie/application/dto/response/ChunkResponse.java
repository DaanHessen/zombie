package dev.daanh.zombie.application.dto.response;

import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.domain.world.enums.ChunkState;

import java.time.LocalDateTime;

public record ChunkResponse(
        ChunkState state,
        ChunkCoordinates coordinates,
        Settlement settlement,
        LocalDateTime generatedAt,
        int generatorVersion
) {
    public static ChunkResponse from(Chunk chunk, Settlement settlement) {
        return new ChunkResponse(
                chunk.getState(),
                chunk.getCoordinates(),
                settlement,
                chunk.getGeneratedAt(),
                chunk.getGeneratorVersion()
        );

    }
}
