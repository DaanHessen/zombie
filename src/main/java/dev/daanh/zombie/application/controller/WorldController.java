package dev.daanh.zombie.application.controller;

import dev.daanh.zombie.application.dto.response.WorldResponse;
import dev.daanh.zombie.application.dto.response.WorldStatsResponse;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.service.WorldService;
import lombok.AllArgsConstructor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/world")
@AllArgsConstructor
public class WorldController {
    private final WorldService worldService;

    @PostMapping("/generate")
    public WorldResponse generate() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("World generation");

        World world = worldService.generate();
        WorldStatsResponse stats = worldService.getWorldStats(world.getId());

        stopWatch.stop();

        return WorldResponse.from(world, stats, stopWatch.getTotalTimeMillis());
    }
}
