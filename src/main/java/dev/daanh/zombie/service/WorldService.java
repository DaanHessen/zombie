package dev.daanh.zombie.service;

import dev.daanh.zombie.application.dto.response.WorldResponse;
import dev.daanh.zombie.application.dto.response.WorldStatsResponse;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.WorldRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class WorldService {
    private final WorldRepository worldRepository;
    private final WorldGenerator worldGenerator;
    private final DataSource dataSource;

    public WorldResponse seedWorld() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        World world = new World();
        world.setName("Earth");
        worldRepository.save(world);
        log.info("World seeded: " + world.getName());

        try (Connection conn = dataSource.getConnection();) {


            worldGenerator.seedWorld(conn, world.getId());
            log.info("World seeded: " + world.getName());
        }

        WorldStatsResponse stats = worldRepository.getWorldStats();

        stopWatch.stop();

        long seedingTimeMs = stopWatch.getTotalTimeMillis();
        log.info("World seeding time: " + seedingTimeMs + "ms");

        return WorldResponse.from(world, stats, seedingTimeMs);
    }
}
