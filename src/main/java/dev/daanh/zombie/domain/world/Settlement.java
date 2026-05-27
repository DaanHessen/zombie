package dev.daanh.zombie.domain.world;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {
    private UUID id;
    private Long geonameId;
    private String name;
    private Coordinates coordinates;
    private String timezone;
    private UUID regionId;
    private String language;
    private int preApocalypsePopulation;
    private boolean groundZero;
    @Builder.Default
    private String biome = "URBAN";
}
