package dev.daanh.zombie.domain.item.records;

import dev.daanh.zombie.domain.item.ItemProfile;
import dev.daanh.zombie.domain.survivor.enums.SkillType;

import java.util.Map;

public record LiteratureProfile(
        Map<SkillType, Integer> xpProvided,
        int ticksToRead
) implements ItemProfile {
}
