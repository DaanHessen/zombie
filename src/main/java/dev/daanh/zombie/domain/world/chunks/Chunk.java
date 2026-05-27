package dev.daanh.zombie.domain.world.chunks;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.enums.ChunkState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import dev.daanh.zombie.domain.item.ItemStack;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chunk extends BaseEntity {
    @Transient
    private ChunkState state = ChunkState.HIBERNATING;

    @Embedded
    private ChunkCoordinates coordinates;

    @Column(name = "settlement_id")
    private UUID settlementId;

    private LocalDateTime generatedAt;

    private int generatorVersion;

    private long generationTimeMs;

    @Transient
    private Settlement settlement;

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }

    public Chunk(ChunkState state, ChunkCoordinates coordinates, UUID settlementId, LocalDateTime generatedAt, int generatorVersion, long generationTimeMs) {
        this.state = state;
        this.coordinates = coordinates;
        this.settlementId = settlementId;
        this.generatedAt = generatedAt;
        this.generatorVersion = generatorVersion;
        this.generationTimeMs = generationTimeMs;
    }
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chunk_id")
    private List<ItemStack> items = new ArrayList<>();
}
