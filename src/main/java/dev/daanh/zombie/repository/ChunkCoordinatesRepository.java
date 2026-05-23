package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChunkCoordinatesRepository extends JpaRepository<ChunkCoordinates, UUID> {
    ChunkCoordinates gpsToChunk(double latitude, double longitude, double chunkSizeKm);
}
