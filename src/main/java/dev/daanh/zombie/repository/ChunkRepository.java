package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    List<Chunk> findByCoordinatesXBetweenAndCoordinatesZBetween(int minX, int maxX, int minZ, int maxZ);
}
