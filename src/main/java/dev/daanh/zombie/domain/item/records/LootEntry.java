package dev.daanh.zombie.domain.item.records;

public record LootEntry(
        ItemCategoryId categoryId,
        int weight,
        int minQuantity,
        int maxQuantity
) {
}
