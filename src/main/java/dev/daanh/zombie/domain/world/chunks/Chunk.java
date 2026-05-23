package dev.daanh.zombie.domain.world.chunks;

import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.world.Settlement;
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
    @Embedded
    private ChunkCoordinates coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    private LocalDateTime generatedAt;

    private int generatorVersion;
}
