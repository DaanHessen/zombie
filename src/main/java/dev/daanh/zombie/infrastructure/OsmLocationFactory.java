package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationSize;
import dev.daanh.zombie.domain.location.enums.LocationType;
import dev.daanh.zombie.domain.world.Coordinates;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OsmLocationFactory {
    
    private final List<OsmLocationMapperStrategy> strategies;

    public OsmLocationFactory(List<OsmLocationMapperStrategy> strategies) {
        this.strategies = strategies;
    }

    public Location createLocation(double lat, double lon, String name, String rawCategory, double areaSqm) {
        LocationSize size = calculateSize(areaSqm);
        
        // Find the right strategy to map the category
        OsmLocationMapperStrategy.LocationMappingResult mapping = null;
        for (OsmLocationMapperStrategy strategy : strategies) {
            if (strategy.matches(rawCategory)) {
                mapping = strategy.map(rawCategory);
                break;
            }
        }
        
        // Fallback if no strategy matched
        if (mapping == null) {
            mapping = new OsmLocationMapperStrategy.LocationMappingResult(
                LocationType.UNKNOWN, 
                LocationCategory.UNKNOWN, 
                null
            );
        }

        Coordinates coords = new Coordinates();
        coords.setLatitude(lat);
        coords.setLongitude(lon);

        Location location = new Location();
        location.setName(name);
        location.setCoordinates(coords);
        location.setType(mapping.type());
        location.setCategory(mapping.category());
        location.setSize(size);
        location.setSubType(mapping.subType());
        
        // Ensure unknown locations are not generated unless specified
        location.setGenerated(false); 
        
        return location;
    }

    private LocationSize calculateSize(double areaSqm) {
        if (areaSqm <= 0.1) return LocationSize.PROPS; // Benches, waste baskets
        if (areaSqm < 30) return LocationSize.VERY_SMALL; // Phone booths, tiny kiosks
        if (areaSqm < 150) return LocationSize.SMALL; // Small cafes, standard repair shops
        if (areaSqm < 400) return LocationSize.MEDIUM; // Average restaurants, standard stores
        if (areaSqm < 1000) return LocationSize.LARGE; // Hotels, supermarkets
        if (areaSqm < 5000) return LocationSize.VERY_LARGE; // Malls, generic hospitals
        return LocationSize.GIANT; // Massive complexes like SuperDARN (50k+ sqm)
    }
}
