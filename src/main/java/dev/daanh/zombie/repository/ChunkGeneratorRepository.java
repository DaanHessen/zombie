package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.chunks.generator.ChunkGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChunkGeneratorRepository extends JpaRepository<ChunkGenerator, UUID> {
}
