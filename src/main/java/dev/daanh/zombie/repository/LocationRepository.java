package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findBySettlementId(UUID settlementId);
}
