package dev.daanh.zombie.domain.world;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
    private UUID id;
    private String name;
    private String code;
    private String capital;
    private int population;
    private double squareKilometers;
    private UUID continentId;
}
