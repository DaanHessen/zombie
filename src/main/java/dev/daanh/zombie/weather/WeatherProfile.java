package dev.daanh.zombie.weather;

import dev.daanh.zombie.world.enums.BiomeType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
public class WeatherProfile {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double rainChance;

    private double stormChance;

    private double snowChance;

    private double fogChance;

    private double windChance;

    @Enumerated(EnumType.STRING)
    private BiomeType biome;

    @Embedded
    @AttributeOverride(name = "celsius", column = @Column(name = "average_temperature"))
    private Temperature averageTemperature;

    @Embedded
    @AttributeOverride(name = "celsius", column = @Column(name = "min_temperature"))
    private Temperature minTemperature;

    @Embedded
    @AttributeOverride(name = "celsius", column = @Column(name = "max_temperature"))
    private Temperature maxTemperature;
}
