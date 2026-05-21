package dev.daanh.zombie.weather;

import dev.daanh.zombie.core.GameTime;
import dev.daanh.zombie.weather.enums.PrecipitationType;
import dev.daanh.zombie.weather.enums.WeatherType;
import dev.daanh.zombie.weather.enums.WindStrength;

import jakarta.persistence.*;

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

    @Embedded
    @AttributeOverride(name = "ticks", column = @Column(name = "started_at_tick"))
    private GameTime startedAt = new GameTime();

    private long durationTicks;

    @Embedded
    private Temperature temperature;

    @Enumerated(EnumType.STRING)
    private WeatherType type;

    @Enumerated(EnumType.STRING)
    private WindStrength windStrength;

    @Enumerated(EnumType.STRING)
    private PrecipitationType precipitationType;

    public boolean isExpired(GameTime time) {
        return time.getTicks() >= (this.startedAt.getTicks() + this.durationTicks);
    }
}
