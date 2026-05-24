package dev.daanh.zombie.service;

import dev.daanh.zombie.application.dto.response.ChunkResponse;
import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.player.PlayerPosition;
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
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ChunkService {
    private final ChunkGenerator chunkGenerator;
    private final ChunkRepository chunkRepository;
    private final PlayerService playerService;
    private final GameConfig config;

    public List<ChunkResponse> getChunks(UUID playerId) {
        List<Chunk> activeGrid = new ArrayList<>();

        PlayerPosition playerPosition = playerService.getPlayerPosition(playerId);
        double latitude = playerPosition.getCoordinates().getLatitude();
        double longitude = playerPosition.getCoordinates().getLongitude();

        int radius = config.getChunk().getChunkGenerationRadius();
        double size = config.getWorld().getChunkSizeKm();

        ChunkCoordinates center = ChunkCoordinates.gpsToChunk(latitude, longitude, size);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;

                ChunkCoordinates coordinates = new ChunkCoordinates(x, z);

                // later on we add caching so check if it exists

                Chunk chunk = chunkRepository.findByCoordinates(coordinates);
                World world = playerPosition.getWorld();

                if (chunk == null) {
                    chunk = chunkGenerator.generate(x, z, world);
                    chunkRepository.save(chunk);
                }
                chunk.setState(ChunkState.ACTIVE);
                activeGrid.add(chunk);
            }
        }

        return activeGrid.stream()
                .map(chunk -> ChunkResponse.from(chunk, null))
                .toList();
    }
}
