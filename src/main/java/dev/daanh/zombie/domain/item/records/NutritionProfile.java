package dev.daanh.zombie.domain.item.records;

import dev.daanh.zombie.domain.item.ItemProfile;

public record NutritionProfile(
        int calories,
        int proteinGrams,
        int carbsGrams,
        int hydrationValue
) implements ItemProfile {
}
