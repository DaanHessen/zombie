package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.SettlementState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementStateRepository extends JpaRepository<SettlementState, UUID> {
}
