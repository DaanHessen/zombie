package dev.daanh.zombie.domain.world;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Continent {
    private UUID id;
    private String name;
    private String code;
}
