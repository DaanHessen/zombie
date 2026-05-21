package dev.daanh.zombie.weather;

import dev.daanh.zombie.weather.enums.PrecipitationType;
import dev.daanh.zombie.weather.enums.WeatherType;
import dev.daanh.zombie.weather.enums.WindStrength;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherState {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double precipitationIntensity;

    private double humidity;

    private double visibility;

    private int severity;

    private long startedAtTick;

    private long durationTicks;

    @Embedded
    private Temperature temperature;

    @Enumerated(EnumType.STRING)
    private WeatherType type;

    @Enumerated(EnumType.STRING)
    private WindStrength windStrength;

    @Enumerated(EnumType.STRING)
    private PrecipitationType precipitationType;

    public boolean isExpired(long currentTick) {
        return currentTick >= (this.startedAtTick + this.durationTicks);
    }
}
