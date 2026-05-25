package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.weather.WeatherState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeatherStateRepository extends JpaRepository<WeatherState, UUID> {
}
