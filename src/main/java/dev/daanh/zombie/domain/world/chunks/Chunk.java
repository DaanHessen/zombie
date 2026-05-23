package dev.daanh.zombie.domain.world.chunks;

import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chunk extends BaseState {
    public static final int GENERATOR_VERSION = 1;

    @Transient
    private ChunkState state = ChunkState.HIBERNATING;

    @Embedded
    private ChunkCoordinates coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    private LocalDateTime generatedAt;

    private int generatorVersion = GENERATOR_VERSION;
}
