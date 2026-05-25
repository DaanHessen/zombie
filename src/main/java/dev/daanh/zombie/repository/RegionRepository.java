package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
