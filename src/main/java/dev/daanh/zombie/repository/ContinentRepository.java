package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContinentRepository extends JpaRepository<Continent, UUID> {
}
