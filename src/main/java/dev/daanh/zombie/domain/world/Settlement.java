package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.domain.world.enums.BiomeType;

import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter(AccessLevel.NONE)
    private Long geonameId;

    private String name;

    @Embedded
    private Coordinates coordinates;

    private String timezone;

    @Enumerated(EnumType.STRING)
    private BiomeType biome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    private String language;

    @Column(name = "population")
    private int preApocalypsePopulation;

    @Column(name = "is_ground_zero")
    private Boolean isGroundZero;

    @Column(name = "is_pois_seeded")
    private Boolean isPoisSeeded;
}
