package dev.daanh.zombie.domain.world.chunks;

import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chunk extends BaseState {
    @Transient
    private ChunkState state = ChunkState.HIBERNATING;

    @Embedded
    private ChunkCoordinates coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    private LocalDateTime generatedAt;

    private int generatorVersion;

    private long generationTimeMs;
}
