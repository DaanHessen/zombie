package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NarrativeServiceTest {

    @Autowired
    private NarrativeService narrativeService;

    @Test
    void testGenerateChunkDescription_Wilderness() {
        Chunk chunk = new Chunk();
        // Null settlement means wilderness
        String description = narrativeService.generateChunkDescription(chunk);
        
        System.out.println("WILDERNESS TEST RESULT: " + description);
        assertThat(description).contains("I'm out in the remote wilderness");
    }

    @Test
    void testGenerateChunkDescription_GroundZero() {
        Settlement settlement = Settlement.builder()
                .name("Lake Vostok")
                .isGroundZero(true)
                .build();
                
        Chunk chunk = new Chunk();
        chunk.setSettlement(settlement);
        
        String description = narrativeService.generateChunkDescription(chunk);
        
        System.out.println("GROUND ZERO TEST RESULT: " + description);
        assertThat(description).contains("ruins of Lake Vostok");
        assertThat(description).contains("terrible feeling about this place");
    }
}
