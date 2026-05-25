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
        // Medical
        registry.put("hospital", new LocationMetadata(LocationType.HOSPITAL, LocationCategory.MEDICAL, LocationSize.LARGE));
        registry.put("clinic", new LocationMetadata(LocationType.CLINIC, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("doctors", new LocationMetadata(LocationType.CLINIC, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("dentist", new LocationMetadata(LocationType.CLINIC, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("pharmacy", new LocationMetadata(LocationType.PHARMACY, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("defibrillator", new LocationMetadata(LocationType.DEFIBRILLATOR, LocationCategory.MEDICAL, LocationSize.SMALL));
        
        // Government/Services
        registry.put("police", new LocationMetadata(LocationType.POLICE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("fire_station", new LocationMetadata(LocationType.FIRE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("fire_hydrant", new LocationMetadata(LocationType.FIRE_HYDRANT, LocationCategory.RESOURCE, LocationSize.SMALL));
        registry.put("townhall", new LocationMetadata(LocationType.TOWN_HALL, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("post_box", new LocationMetadata(LocationType.POST_BOX, LocationCategory.INTERACTIVE, LocationSize.SMALL));

        // Commercial
        registry.put("supermarket", new LocationMetadata(LocationType.SUPERMARKET, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("convenience", new LocationMetadata(LocationType.GENERAL_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("restaurant", new LocationMetadata(LocationType.RESTAURANT, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("fast_food", new LocationMetadata(LocationType.FAST_FOOD, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("cafe", new LocationMetadata(LocationType.CAFE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("pub", new LocationMetadata(LocationType.PUB, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("bar", new LocationMetadata(LocationType.PUB, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("bakery", new LocationMetadata(LocationType.BAKERY, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("hairdresser", new LocationMetadata(LocationType.HAIRDRESSER, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("car_repair", new LocationMetadata(LocationType.CAR_REPAIR, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("fuel", new LocationMetadata(LocationType.GAS_STATION, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("atm", new LocationMetadata(LocationType.ATM, LocationCategory.INTERACTIVE, LocationSize.SMALL));
        registry.put("vending_machine", new LocationMetadata(LocationType.VENDING_MACHINE, LocationCategory.INTERACTIVE, LocationSize.SMALL));

        // Education
        registry.put("school", new LocationMetadata(LocationType.SCHOOL, LocationCategory.EDUCATION, LocationSize.MEDIUM));
        registry.put("university", new LocationMetadata(LocationType.UNIVERSITY, LocationCategory.EDUCATION, LocationSize.LARGE));
        registry.put("college", new LocationMetadata(LocationType.UNIVERSITY, LocationCategory.EDUCATION, LocationSize.LARGE));

        // Religious
        registry.put("place_of_worship", new LocationMetadata(LocationType.CHURCH, LocationCategory.RELIGIOUS, LocationSize.MEDIUM));
        registry.put("grave_yard", new LocationMetadata(LocationType.CEMETERY, LocationCategory.RELIGIOUS, LocationSize.MEDIUM));

        // Parks & Rec
        registry.put("park", new LocationMetadata(LocationType.PARK, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("pitch", new LocationMetadata(LocationType.PITCH, LocationCategory.RECREATIONAL, LocationSize.MEDIUM));
        registry.put("playground", new LocationMetadata(LocationType.PLAYGROUND, LocationCategory.RECREATIONAL, LocationSize.SMALL));
        registry.put("camp_site", new LocationMetadata(LocationType.CAMPSITE, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("bench", new LocationMetadata(LocationType.BENCH, LocationCategory.INTERACTIVE, LocationSize.SMALL));

        // Resources / Survival
        registry.put("shelter", new LocationMetadata(LocationType.SHELTER, LocationCategory.SURVIVAL, LocationSize.SMALL));
        registry.put("drinking_water", new LocationMetadata(LocationType.DRINKING_WATER, LocationCategory.RESOURCE, LocationSize.SMALL));
        registry.put("toilets", new LocationMetadata(LocationType.TOILETS, LocationCategory.RESOURCE, LocationSize.SMALL));
        registry.put("waste_basket", new LocationMetadata(LocationType.WASTE_BASKET, LocationCategory.RESOURCE, LocationSize.SMALL));
        registry.put("waste_disposal", new LocationMetadata(LocationType.WASTE_DISPOSAL, LocationCategory.RESOURCE, LocationSize.MEDIUM));
        registry.put("recycling", new LocationMetadata(LocationType.RECYCLING, LocationCategory.RESOURCE, LocationSize.MEDIUM));
    }

    public LocationMetadata resolve(String category) {
        if (category == null || category.isBlank()) {
            return getDefaultFallback();
        }
        return registry.getOrDefault(category.toLowerCase(), getDefaultFallback());
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
