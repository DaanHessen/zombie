package dev.daanh.zombie.domain.item;

import dev.daanh.zombie.domain.item.ItemProfile;
import dev.daanh.zombie.domain.item.records.ItemCategoryId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemTemplate {

    private UUID id;

    private String name;

    private String description;

    private ItemCategoryId categoryId;

    private int weightGrams;

    private int maxStackSize;

    private boolean instanceTracked;

    private double volumeLitres;

    @Builder.Default
    private Map<Class<? extends ItemProfile>, ItemProfile> profiles = Map.of();

    public Map<Class<? extends ItemProfile>, ItemProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }
}
