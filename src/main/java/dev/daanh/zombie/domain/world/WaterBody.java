package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.domain.world.enums.WaterBodyType;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterBody {
    private UUID id;
    private String name;
    private WaterBodyType type;
}
