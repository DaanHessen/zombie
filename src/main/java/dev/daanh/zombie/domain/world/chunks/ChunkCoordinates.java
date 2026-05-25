package dev.daanh.zombie.domain.world.chunks;

import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.world.Coordinates;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ChunkCoordinates {
    private int x;
    private int z;

    // maybe use "web mercator projection" later? might be more accurate.
    public static ChunkCoordinates gpsToChunk(double latitude, double longitude, double chunkSizeKm) {
        int x = (int) Math.floor((longitude * 111.0) / chunkSizeKm);
        int z = (int) Math.floor((latitude * 111.0) / chunkSizeKm);

        return new ChunkCoordinates(x, z);
    }

    public Coordinates getMinCoordinates(double chunkSizeKm) {
        return new Coordinates( (z * chunkSizeKm) / 111.0, (x * chunkSizeKm) / 111.0 );
    }
    public Coordinates getMaxCoordinates(double chunkSizeKm) {
        return new Coordinates( ((z + 1) * chunkSizeKm) / 111.0, ((x + 1) * chunkSizeKm) / 111.0 );
    }
}
