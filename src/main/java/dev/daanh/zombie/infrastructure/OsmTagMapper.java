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
        registry.put("chemist", new LocationMetadata(LocationType.PHARMACY, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("defibrillator", new LocationMetadata(LocationType.DEFIBRILLATOR, LocationCategory.MEDICAL, LocationSize.SMALL));
        registry.put("veterinary", new LocationMetadata(LocationType.CLINIC, LocationCategory.MEDICAL, LocationSize.SMALL));
        
        // Government/Services
        registry.put("police", new LocationMetadata(LocationType.POLICE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("fire_station", new LocationMetadata(LocationType.FIRE_STATION, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("fire_hydrant", new LocationMetadata(LocationType.FIRE_HYDRANT, LocationCategory.RESOURCE, LocationSize.SMALL));
        registry.put("townhall", new LocationMetadata(LocationType.TOWN_HALL, LocationCategory.GOVERNMENT, LocationSize.MEDIUM));
        registry.put("post_office", new LocationMetadata(LocationType.TOWN_HALL, LocationCategory.GOVERNMENT, LocationSize.SMALL));

        // Commercial (Food)
        registry.put("supermarket", new LocationMetadata(LocationType.SUPERMARKET, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("convenience", new LocationMetadata(LocationType.GENERAL_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("restaurant", new LocationMetadata(LocationType.RESTAURANT, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("fast_food", new LocationMetadata(LocationType.FAST_FOOD, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("cafe", new LocationMetadata(LocationType.CAFE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("pub", new LocationMetadata(LocationType.PUB, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("bar", new LocationMetadata(LocationType.PUB, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("bakery", new LocationMetadata(LocationType.BAKERY, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("butcher", new LocationMetadata(LocationType.GENERAL_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));

        // Commercial (Other Top Categories)
        registry.put("hairdresser", new LocationMetadata(LocationType.SPECIALIZED_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("beauty", new LocationMetadata(LocationType.SPECIALIZED_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL));
        registry.put("car_repair", new LocationMetadata(LocationType.CAR_REPAIR, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("fuel", new LocationMetadata(LocationType.GAS_STATION, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        registry.put("atm", new LocationMetadata(LocationType.ATM, LocationCategory.INTERACTIVE, LocationSize.SMALL));
        registry.put("doityourself", new LocationMetadata(LocationType.WAREHOUSE, LocationCategory.COMMERCIAL, LocationSize.LARGE));
        registry.put("furniture", new LocationMetadata(LocationType.WAREHOUSE, LocationCategory.COMMERCIAL, LocationSize.LARGE));
        registry.put("car_wash", new LocationMetadata(LocationType.SPECIALIZED_REPAIR_SHOP, LocationCategory.COMMERCIAL, LocationSize.MEDIUM));
        
        // Education
        registry.put("school", new LocationMetadata(LocationType.SCHOOL, LocationCategory.EDUCATION, LocationSize.MEDIUM));
        registry.put("kindergarten", new LocationMetadata(LocationType.SCHOOL, LocationCategory.EDUCATION, LocationSize.SMALL));
        registry.put("university", new LocationMetadata(LocationType.UNIVERSITY, LocationCategory.EDUCATION, LocationSize.LARGE));
        registry.put("college", new LocationMetadata(LocationType.UNIVERSITY, LocationCategory.EDUCATION, LocationSize.LARGE));

        // Religious
        registry.put("place_of_worship", new LocationMetadata(LocationType.CHURCH, LocationCategory.RELIGIOUS, LocationSize.MEDIUM));
        registry.put("grave_yard", new LocationMetadata(LocationType.CEMETERY, LocationCategory.RELIGIOUS, LocationSize.MEDIUM));

        // Parks & Rec
        registry.put("park", new LocationMetadata(LocationType.PARK, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("garden", new LocationMetadata(LocationType.PARK, LocationCategory.RECREATIONAL, LocationSize.MEDIUM));
        registry.put("nature_reserve", new LocationMetadata(LocationType.PARK, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("pitch", new LocationMetadata(LocationType.PITCH, LocationCategory.RECREATIONAL, LocationSize.MEDIUM));
        registry.put("playground", new LocationMetadata(LocationType.PLAYGROUND, LocationCategory.RECREATIONAL, LocationSize.SMALL));
        registry.put("camp_site", new LocationMetadata(LocationType.CAMPSITE, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("sports_centre", new LocationMetadata(LocationType.PITCH, LocationCategory.RECREATIONAL, LocationSize.LARGE));
        registry.put("swimming_pool", new LocationMetadata(LocationType.PITCH, LocationCategory.RECREATIONAL, LocationSize.MEDIUM));

        // Interactive / Minor Props
        registry.put("bench", new LocationMetadata(LocationType.BENCH, LocationCategory.INTERACTIVE, LocationSize.SMALL));
        registry.put("post_box", new LocationMetadata(LocationType.POST_BOX, LocationCategory.INTERACTIVE, LocationSize.SMALL));
        registry.put("vending_machine", new LocationMetadata(LocationType.VENDING_MACHINE, LocationCategory.INTERACTIVE, LocationSize.SMALL));
        
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
        
        category = category.toLowerCase();

        // 1. Check explicit registry for exactly mapped items
        if (registry.containsKey(category)) {
            return registry.get(category);
        }

        // 2. Dynamic Keyword Fallbacks for the thousands of crazy OSM tags
        if (category.contains("repair") || category.contains("mechanic")) {
            return new LocationMetadata(LocationType.SPECIALIZED_REPAIR_SHOP, LocationCategory.COMMERCIAL, LocationSize.SMALL);
        }
        if (category.contains("club") || category.contains("nightclub") || category.contains("hookah")) {
            return new LocationMetadata(LocationType.ENTERTAINMENT_CLUB, LocationCategory.COMMERCIAL, LocationSize.MEDIUM);
        }
        if (category.contains("clothes") || category.contains("shoes") || category.contains("boutique") || category.contains("tailor") || category.contains("jewel")) {
            return new LocationMetadata(LocationType.CLOTHING_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL);
        }
        if (category.contains("store") || category.contains("shop") || category.contains("market")) {
            return new LocationMetadata(LocationType.SPECIALIZED_STORE, LocationCategory.COMMERCIAL, LocationSize.SMALL);
        }

        // 3. Exact Enum Match Check (e.g. if the category is literally "bench" or "hospital")
        try {
            LocationType exactType = LocationType.valueOf(category.toUpperCase());
            // If it matches an enum exactly, we assign a generic category but preserve the exact Type!
            return new LocationMetadata(exactType, LocationCategory.UNKNOWN, LocationSize.SMALL);
        } catch (IllegalArgumentException e) {
            // It's not a direct enum match, ignore and continue to default fallback
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
