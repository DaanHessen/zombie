package dev.daanh.zombie.domain.item.records;

import java.util.Locale;
import java.util.Objects;

public record ItemGroupId(String value) {

    public ItemGroupId {
        Objects.requireNonNull(value, "ItemGroupId cannot be null");

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isBlank()) {
            throw new IllegalArgumentException("ItemGroupId cannot be blank");
        }
    }
}
