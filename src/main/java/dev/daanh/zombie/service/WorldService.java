package dev.daanh.zombie.service;

import dev.daanh.zombie.application.dto.response.WorldStatsResponse;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.repository.WorldRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class WorldService {
    private final WorldRepository worldRepository;
    private final SettlementService settlementService;

    public World generate() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("World generation");

        World world = new World();
        world.setName("Earth");
        worldRepository.save(world);

        settlementService.generateSettlementStates(world);

        stopWatch.stop();
        log.info("World generation completed in {} ms", stopWatch.getTotalTimeMillis());

        return world;
    }

    public WorldStatsResponse getWorldStats(UUID worldId) {
        return worldRepository.getWorldStats(worldId);
    }
}
