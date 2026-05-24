package dev.daanh.zombie.application.controller;

import dev.daanh.zombie.application.dto.response.ChunkResponse;
import dev.daanh.zombie.domain.player.PlayerPosition;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.service.ChunkService;
import dev.daanh.zombie.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chunk")
@AllArgsConstructor
public class ChunkController {
    private final ChunkService chunkService;
    private final PlayerService playerService;

    @RequestMapping("/chunks")
    public List<ChunkResponse> getChunks(@RequestHeader UUID playerId) {
        PlayerPosition playerPosition = playerService.getPlayerPosition(playerId);
        List<Chunk> chunks = chunkService.getChunks(playerPosition.getCoordinates(), playerPosition.getWorld());
        return chunks.stream()
                .map(chunk -> ChunkResponse.from(chunk, null))
                .toList();
    }
}
