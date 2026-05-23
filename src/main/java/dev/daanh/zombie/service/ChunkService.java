package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.player.PlayerPosition;
import dev.daanh.zombie.domain.world.chunks.generator.ChunkGenerator;
import dev.daanh.zombie.repository.ChunkRepository;
import dev.daanh.zombie.service.dto.ChunkResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ChunkService {
    private final ChunkGenerator chunkGenerator;
    private final ChunkRepository chunkRepository;
    private final PlayerService playerService;

    public List<ChunkResponse> loadOrGenerateSurroundingChunk(int x, int z, UUID playerId) {
        PlayerPosition playerPosition = playerService.getPlayerPosition(playerId);

        // continue tomorrow
    }
}
