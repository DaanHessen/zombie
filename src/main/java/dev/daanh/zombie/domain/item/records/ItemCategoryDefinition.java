package dev.daanh.zombie.domain.item.records;

public record ItemCategoryDefinition(
        ItemCategoryId id,
        ItemGroupId groupId,
        String displayName
) {
}
