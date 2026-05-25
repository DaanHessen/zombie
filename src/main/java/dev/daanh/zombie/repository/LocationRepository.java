package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
