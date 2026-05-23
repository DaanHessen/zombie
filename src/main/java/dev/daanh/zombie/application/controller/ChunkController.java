package dev.daanh.zombie.application.controller;

import dev.daanh.zombie.application.dto.response.ChunkResponse;
import dev.daanh.zombie.service.ChunkService;
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

    @RequestMapping("/chunks")
    public List<ChunkResponse> getChunks(@RequestHeader UUID playerId) {
        return chunkService.getChunks(playerId);
    }
}
