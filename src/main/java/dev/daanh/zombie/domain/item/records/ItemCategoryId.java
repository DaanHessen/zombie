package dev.daanh.zombie.domain.item.records;

import java.util.Locale;
import java.util.Objects;

public record ItemCategoryId(String value) {

    public ItemCategoryId {
        Objects.requireNonNull(value, "ItemCategoryId cannot be null");

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isBlank()) {
            throw new IllegalArgumentException("ItemCategoryId cannot be blank");
        }
    }
}
