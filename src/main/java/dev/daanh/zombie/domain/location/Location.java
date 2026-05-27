package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.location.enums.AccessState;
import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationSize;
import dev.daanh.zombie.domain.location.enums.LocationType;
import dev.daanh.zombie.domain.world.Coordinates;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private UUID id;
    private String name;
    private LocationType type;
    private LocationSize size;
    private LocationCategory category;
    private String subType;
    private Coordinates coordinates;
    private boolean indoors;
    private boolean generated;
    private UUID regionId;
    private UUID settlementId;
    private boolean isLooted;
}
