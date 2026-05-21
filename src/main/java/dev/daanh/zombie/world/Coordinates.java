package dev.daanh.zombie.world;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private Double latitude;

    private Double longitude;

    private int elevation;
}
