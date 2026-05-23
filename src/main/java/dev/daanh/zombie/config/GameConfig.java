package dev.daanh.zombie.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "game")
public class GameConfig {
    private World world = new World();
    private Simulation simulation = new Simulation();

    @Getter @Setter
    public static class World {
        private double chunkSizeKm;
        private String name;
        private long defaultSeed;
    }

    @Getter @Setter
    public static class Simulation {
        private int tickRateMs;
        private int minutesPerTick;
        private String startTime;
    }
}
