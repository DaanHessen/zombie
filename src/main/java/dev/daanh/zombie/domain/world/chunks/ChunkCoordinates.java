package dev.daanh.zombie.domain.world.chunks;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ChunkCoordinates {
    private int x;
    private int z;
}
