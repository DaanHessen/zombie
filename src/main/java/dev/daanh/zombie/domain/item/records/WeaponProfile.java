package dev.daanh.zombie.domain.item.records;

import dev.daanh.zombie.domain.item.ItemProfile;

public record WeaponProfile(
        double baseDamage,
        double durabilityLossPerUse,
        double noiseRadiusMeters
) implements ItemProfile {
}
