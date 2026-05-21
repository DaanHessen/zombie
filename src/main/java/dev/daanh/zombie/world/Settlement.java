package dev.daanh.zombie.world;

import dev.daanh.zombie.world.enums.BiomeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long geonameId;

    private String name;

    @Embedded
    private Coordinates coordinates;

    private int population;

    // maybe add language in the future too?
    private String timezone;

    @Enumerated(EnumType.STRING)
    private BiomeType biome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;
}
