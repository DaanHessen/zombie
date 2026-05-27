package dev.daanh.zombie.domain.item.records;

import dev.daanh.zombie.domain.item.ItemProfile;

public record SpoilageProfile(
        long baseSpoilageTicks
) implements ItemProfile {
}
