package dev.daanh.zombie.weather;

import dev.daanh.zombie.world.Region;
import dev.daanh.zombie.world.Settlement;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private long updatedAtTick;

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
