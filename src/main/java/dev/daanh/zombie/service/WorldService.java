package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.WorldRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
@AllArgsConstructor
@Slf4j
public class WorldService {
    private final WorldRepository worldRepository;
    private final WorldGenerator worldGenerator;
    private final DataSource dataSource;

    public void seedWorld() throws Exception {
        World world = new World();
        world.setName("Earth");
        worldRepository.save(world);
        log.info("World generated: " + world.getName());

        try (Connection conn = dataSource.getConnection();) {

            worldGenerator.seedWorld(conn, world.getId());
            log.info("World seeded: " + world.getName());
        }
    }
}
