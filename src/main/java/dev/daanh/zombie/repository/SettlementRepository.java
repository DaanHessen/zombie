package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
}
