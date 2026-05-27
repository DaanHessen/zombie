package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationType;

public interface OsmLocationMapperStrategy {
    
    /**
     * Determines if this strategy can handle the given raw OSM category string.
     */
    boolean matches(String rawCategory);

    /**
     * Maps the raw category to a LocationType, LocationCategory, and SubType.
     */
    LocationMappingResult map(String rawCategory);

    record LocationMappingResult(
            LocationType type,
            LocationCategory category,
            String subType // Can be null, e.g. "BICYCLE", "SHOE", "NIGHTCLUB"
    ) {}
}
