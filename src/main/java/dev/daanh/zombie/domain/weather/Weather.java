package dev.daanh.zombie.domain.weather;

import dev.daanh.zombie.domain.core.GameTime;
import dev.daanh.zombie.domain.world.Region;
import dev.daanh.zombie.domain.world.Settlement;

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
public class Weather {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "ticks", column = @Column(name = "updated_at_tick"))
    private GameTime updatedAt = new GameTime();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    @OneToOne(fetch = FetchType.LAZY)
    private WeatherState currentState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_profile_id")
    private WeatherProfile profile;
}
