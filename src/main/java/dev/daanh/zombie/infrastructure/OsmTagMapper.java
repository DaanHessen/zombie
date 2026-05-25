package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationSize;
import dev.daanh.zombie.domain.location.enums.LocationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OsmTagMapper {
    private final Map<String, LocationMetadata> registry = new HashMap<>();

    public OsmTagMapper() {
        registry.put("amenity=hospital", new LocationMetadata(LocationType.HOSPITAL, LocationCategory.MEDICAL, LocationSize.LARGE));
        registry.put("amenity=clinic", new LocationMetadata(LocationType.CLINIC, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("amenity=pharmacy", new LocationMetadata(LocationType.PHARMACY, LocationCategory.MEDICAL, LocationSize.SMALL));
        
        registry.put("amenity=police", new LocationMetadata(LocationType.POLICE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("amenity=fire_station", new LocationMetadata(LocationType.FIRE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("military=base", new LocationMetadata(LocationType.MILITARY_BASE, LocationCategory.MILITARY, LocationSize.LARGE));

        registry.put("shop=supermarket", new LocationMetadata(LocationType.SUPERMARKET, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("shop=convenience", new LocationMetadata(LocationType.GENERAL_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("amenity=restaurant", new LocationMetadata(LocationType.RESTAURANT, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("amenity=fast_food", new LocationMetadata(LocationType.RESTAURANT, LocationCategory.COMMERCIAL, LocationSize.SMALL));

        registry.put("amenity=school", new LocationMetadata(LocationType.SCHOOL, LocationCategory.EDUCATION, LocationSize.MEDIUM));
        registry.put("amenity=university", new LocationMetadata(LocationType.UNIVERSITY, LocationCategory.EDUCATION, LocationSize.LARGE));

        registry.put("amenity=bank", new LocationMetadata(LocationType.UNKNOWN, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("amenity=place_of_worship", new LocationMetadata(LocationType.CHURCH, LocationCategory.RELIGIOUS, LocationSize.MEDIUM));
    }

    public LocationMetadata resolve(Map<String, String> tags) {
        if (tags == null) return getDefaultFallback();

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String tagKey = entry.getKey() + "=" + entry.getValue();
            if (registry.containsKey(tagKey)) {
                return registry.get(tagKey);
            }
        }
        
        return getDefaultFallback();
    }

    private LocationMetadata getDefaultFallback() {
        return new LocationMetadata(LocationType.UNKNOWN, LocationCategory.UNKNOWN, LocationSize.SMALL);
    }

    public record LocationMetadata(
            LocationType type,
            LocationCategory category,
            LocationSize size
    ) {}
}
