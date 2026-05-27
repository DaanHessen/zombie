package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.survivor.Survivor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurvivorRepository extends JpaRepository<Survivor, UUID> {
    List<Survivor> findByWorldIdAndDeletedFalse(UUID worldId);
}
