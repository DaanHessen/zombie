package dev.daanh.zombie.service;

import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.domain.world.chunks.generator.ChunkGenerator;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import dev.daanh.zombie.repository.ChunkRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ChunkService {
    private final ChunkGenerator chunkGenerator;
    private final ChunkRepository chunkRepository;
    private final GameConfig config;

    public List<Chunk> getChunks(Coordinates coordinates, World world) {
        List<Chunk> activeGrid = new ArrayList<>();

        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();

        int radius = config.getChunk().getChunkGenerationRadius();
        double size = config.getWorld().getChunkSizeKm();

        ChunkCoordinates center = ChunkCoordinates.gpsToChunk(latitude, longitude, size);

        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;

        List<Chunk> cachedChunks = chunkRepository.findByCoordinatesXBetweenAndCoordinatesZBetween(minX, maxX, minZ, maxZ);
        Map<ChunkCoordinates, Chunk> cachedChunksMap = cachedChunks.stream()
                .collect(Collectors.toMap(Chunk::getCoordinates, chunk -> chunk));

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;

                ChunkCoordinates chunkCoordinates = new ChunkCoordinates(x, z);

                Chunk chunk = cachedChunksMap.get(chunkCoordinates);

                if (chunk == null) {
                    chunk = chunkGenerator.generate(x, z, world);
                    chunkRepository.save(chunk);
                }
                chunk.setState(ChunkState.ACTIVE);
                activeGrid.add(chunk);
            }
        }

        return activeGrid;
    }
}
