package dev.daanh.zombie.service;

import dev.daanh.zombie.application.dto.response.WorldResponse;
import dev.daanh.zombie.application.dto.response.WorldStatsResponse;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.SettlementState;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.WorldRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class WorldService {
    private final WorldRepository worldRepository;
    private final SettlementService settlementService;

    public WorldResponse generate() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("World generation");

        World world = new World();
        world.setName("Earth");
        worldRepository.save(world);

        settlementService.generateSettlementStates(world);

        WorldStatsResponse stats = worldRepository.getWorldStats(world.getId());

        stopWatch.stop();
        log.info("World generation completed in {} ms", stopWatch.getTotalTimeMillis());

        return WorldResponse.from(world, stats, stopWatch.getTotalTimeMillis());
    }
}
