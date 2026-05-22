package dev.daanh.zombie.domain.world;

import jakarta.persistence.Embeddable;

import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinates {
    private Double latitude;

    private Double longitude;

//    private int elevation;


}
