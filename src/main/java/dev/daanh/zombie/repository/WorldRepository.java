package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.World;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorldRepository extends JpaRepository<World, UUID> {
}
