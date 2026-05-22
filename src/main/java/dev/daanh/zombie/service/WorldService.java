package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.WorldRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorldService {
    private final WorldRepository worldRepository;
    private final WorldGenerator worldGenerator;

    public void generateWorld(long seed) {
        World world = worldGenerator.generateWorld(seed);
        worldRepository.save(world);
    }
}
