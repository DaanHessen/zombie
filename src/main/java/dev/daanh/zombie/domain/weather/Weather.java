package dev.daanh.zombie.domain.weather;

import dev.daanh.zombie.domain.core.BaseEntity;
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
public class Weather extends BaseEntity {
    @Embedded
    @AttributeOverride(name = "ticks", column = @Column(name = "updated_at_tick"))
    private GameTime updatedAtTick = new GameTime();

    @Column(name = "region_id")
    private UUID regionId;

    @Column(name = "settlement_id")
    private UUID settlementId;

    @OneToOne(fetch = FetchType.LAZY)
    private WeatherState currentState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_profile_id")
    private WeatherProfile profile;
}
