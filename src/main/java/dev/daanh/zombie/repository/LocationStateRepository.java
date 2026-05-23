package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.location.LocationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationStateRepository extends JpaRepository<LocationState, UUID> {
}
