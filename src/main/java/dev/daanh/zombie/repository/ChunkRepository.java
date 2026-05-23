package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.chunks.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
}
