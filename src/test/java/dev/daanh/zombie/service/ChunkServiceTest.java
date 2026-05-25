package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.generator.ChunkGenerator;
import dev.daanh.zombie.repository.WorldRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ChunkServiceTest {

    @Autowired
    private ChunkService chunkService;

    @Autowired
    private WorldRepository worldRepository;
    
    @Autowired
    private dev.daanh.zombie.repository.ChunkRepository chunkRepository;

    @MockitoSpyBean
    private ChunkGenerator chunkGenerator;

    @Test
    void testChunkCachingMechanism() {
        chunkRepository.deleteAll(); // Clear state from previous runs
        
        World world = new World();
        world.setName("Caching Test World");
        world = worldRepository.save(world);

        Coordinates coords = new Coordinates(50.0, 5.0);

        List<Chunk> firstCall = chunkService.getChunks(coords, world);
        System.out.println("First call generated/retrieved " + firstCall.size() + " chunks.");
        
        verify(chunkGenerator, times(firstCall.size())).generate(anyInt(), anyInt(), any(World.class));

        List<Chunk> secondCall = chunkService.getChunks(coords, world);
        System.out.println("Second call retrieved " + secondCall.size() + " chunks.");

        verify(chunkGenerator, times(firstCall.size())).generate(anyInt(), anyInt(), any(World.class));
    }
}
