package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByNameIgnoreCase(String name);

    List<Settlement> findByCoordinatesLatitudeBetweenAndCoordinatesLongitudeBetween(
            double minLat, double maxLat,
            double minLon, double maxLon
    );
}
