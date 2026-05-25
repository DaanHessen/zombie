package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
