package dev.daanh.zombie.config;

import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.ContinentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Slf4j
@AllArgsConstructor
public class DatabaseInitializer {
    private final WorldGenerator worldGenerator;
    private final ContinentRepository continentRepository;
    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (continentRepository.count() > 0) {
            log.info("Skipping database initialization: database already initialized");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("Initializing database...");

        try (Connection conn = dataSource.getConnection()) {
            worldGenerator.seedWorld(conn, null);
            stopWatch.stop();
            log.info("Database initialized in: " + stopWatch.getTotalTimeMillis() + "ms");
        } catch (Exception e) {
            log.error("Error initializing database", e);
        }
    }
}
