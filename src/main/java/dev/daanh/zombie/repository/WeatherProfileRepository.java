package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.weather.WeatherProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherProfileRepository extends JpaRepository<WeatherProfile, Long> {
}
