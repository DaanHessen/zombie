package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultOsmMapperStrategy implements OsmLocationMapperStrategy {

    private final Map<String, LocationMappingResult> exactRegistry = new HashMap<>();

    public DefaultOsmMapperStrategy() {
        // Core survival explicit maps
        exactRegistry.put("hospital", new LocationMappingResult(LocationType.HOSPITAL, LocationCategory.MEDICAL, null));
        exactRegistry.put("clinic", new LocationMappingResult(LocationType.CLINIC, LocationCategory.MEDICAL, null));
        exactRegistry.put("pharmacy", new LocationMappingResult(LocationType.PHARMACY, LocationCategory.MEDICAL, null));
        exactRegistry.put("police", new LocationMappingResult(LocationType.POLICE_STATION, LocationCategory.GOVERNMENT, null));
        exactRegistry.put("fire_station", new LocationMappingResult(LocationType.FIRE_STATION, LocationCategory.GOVERNMENT, null));
        exactRegistry.put("supermarket", new LocationMappingResult(LocationType.SUPERMARKET, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("convenience", new LocationMappingResult(LocationType.GENERAL_STORE, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("restaurant", new LocationMappingResult(LocationType.RESTAURANT, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("fast_food", new LocationMappingResult(LocationType.FAST_FOOD, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("fuel", new LocationMappingResult(LocationType.GAS_STATION, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("school", new LocationMappingResult(LocationType.SCHOOL, LocationCategory.EDUCATION, null));
        
        // Interactive / Minor Props
        exactRegistry.put("bench", new LocationMappingResult(LocationType.BENCH, LocationCategory.INTERACTIVE, null));
        exactRegistry.put("waste_basket", new LocationMappingResult(LocationType.WASTE_BASKET, LocationCategory.RESOURCE, null));
        
        // Missing commercial and other locations
        exactRegistry.put("cafe", new LocationMappingResult(LocationType.CAFE, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("pub", new LocationMappingResult(LocationType.PUB, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("bar", new LocationMappingResult(LocationType.PUB, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("bakery", new LocationMappingResult(LocationType.BAKERY, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("hairdresser", new LocationMappingResult(LocationType.HAIRDRESSER, LocationCategory.COMMERCIAL, null));
        exactRegistry.put("church", new LocationMappingResult(LocationType.CHURCH, LocationCategory.RELIGIOUS, null));
        exactRegistry.put("cemetery", new LocationMappingResult(LocationType.CEMETERY, LocationCategory.RELIGIOUS, null));
        exactRegistry.put("park", new LocationMappingResult(LocationType.PARK, LocationCategory.RECREATIONAL, null));
        exactRegistry.put("playground", new LocationMappingResult(LocationType.PLAYGROUND, LocationCategory.RECREATIONAL, null));
    }

    @Override
    public boolean matches(String rawCategory) {
        // This is the fallback strategy, so it catches everything
        return true; 
    }

    @Override
    public LocationMappingResult map(String rawCategory) {
        String category = rawCategory.toLowerCase();

        // 1. Explicit Matches
        if (exactRegistry.containsKey(category)) {
            return exactRegistry.get(category);
        }

        // 2. Dynamic Keyword Fallbacks
        if (category.contains("repair") || category.contains("mechanic")) {
            String subType = category.replace("_repair", "").replace("repair", "").trim();
            if (subType.isEmpty()) subType = "GENERIC";
            return new LocationMappingResult(LocationType.SPECIALIZED_REPAIR_SHOP, LocationCategory.COMMERCIAL, subType.toUpperCase());
        }
        
        if (category.contains("club") || category.contains("nightclub") || category.contains("hookah")) {
            return new LocationMappingResult(LocationType.ENTERTAINMENT_CLUB, LocationCategory.COMMERCIAL, "NIGHTCLUB");
        }
        
        if (category.contains("clothes") || category.contains("shoes") || category.contains("boutique") || category.contains("tailor") || category.contains("jewel")) {
            return new LocationMappingResult(LocationType.CLOTHING_STORE, LocationCategory.COMMERCIAL, null);
        }
        
        if (category.contains("store") || category.contains("shop") || category.contains("market")) {
            String subType = category.replace("_store", "").replace("_shop", "").trim();
            if (subType.isEmpty()) subType = "GENERIC";
            return new LocationMappingResult(LocationType.SPECIALIZED_STORE, LocationCategory.COMMERCIAL, subType.toUpperCase());
        }

        // 3. Exact Enum check
        try {
            LocationType exactType = LocationType.valueOf(category.toUpperCase());
            return new LocationMappingResult(exactType, LocationCategory.UNKNOWN, null);
        } catch (IllegalArgumentException e) {
            // Not a direct match
        }

        // Default Unknown
        return new LocationMappingResult(LocationType.UNKNOWN, LocationCategory.UNKNOWN, null);
    }
}
