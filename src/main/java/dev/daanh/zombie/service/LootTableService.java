package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.item.ItemStack;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.records.ItemCategoryId;
import dev.daanh.zombie.domain.item.records.LootEntry;
import dev.daanh.zombie.repository.ItemTemplateRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class LootTableService {

    private final ItemTemplateRepository itemTemplateRepository;
    private final Map<String, List<LootEntry>> tables = new HashMap<>();

    @PostConstruct
    public void init() {
        // JSON...
        tables.put("URBAN", List.of(
                new LootEntry(new ItemCategoryId("food_canned"), 40, 1, 3),
                new LootEntry(new ItemCategoryId("water_bottled"), 30, 1, 2),
                new LootEntry(new ItemCategoryId("medicine_basic"), 10, 1, 1),
                new LootEntry(new ItemCategoryId("weapon_melee"), 15, 1, 1),
                new LootEntry(new ItemCategoryId("magazine_medical"), 5, 1, 1)
        ));

        tables.put("RURAL", List.of(
                new LootEntry(new ItemCategoryId("food_fresh"), 30, 1, 4),
                new LootEntry(new ItemCategoryId("water_purified"), 20, 1, 2),
                new LootEntry(new ItemCategoryId("weapon_firearm_hunting"), 10, 1, 1),
                new LootEntry(new ItemCategoryId("magazine_farming"), 5, 1, 1)
        ));

        tables.put("HOSPITAL", List.of(
                new LootEntry(new ItemCategoryId("medicine_basic"), 50, 1, 4),
                new LootEntry(new ItemCategoryId("medicine_advanced"), 30, 1, 2),
                new LootEntry(new ItemCategoryId("magazine_medical"), 15, 1, 2)
        ));
    }

    public List<ItemStack> rollLoot(String tableName, int rolls) {
        List<LootEntry> table = tables.getOrDefault(tableName.toUpperCase(), tables.get("URBAN"));
        List<ItemStack> droppedItems = new ArrayList<>();

        int totalWeight = table.stream().mapToInt(LootEntry::weight).sum();
        if (totalWeight == 0) return droppedItems;

        for (int i = 0; i < rolls; i++) {
            int roll = ThreadLocalRandom.current().nextInt(totalWeight);
            int currentWeight = 0;

            for (LootEntry entry : table) {
                currentWeight += entry.weight();
                if (roll < currentWeight) {
                    List<ItemTemplate> possibleTemplates = itemTemplateRepository.findByType(entry.categoryId());
                    if (!possibleTemplates.isEmpty()) {
                        ItemTemplate template = possibleTemplates.get(ThreadLocalRandom.current().nextInt(possibleTemplates.size()));
                        int quantity = ThreadLocalRandom.current().nextInt(entry.minQuantity(), entry.maxQuantity() + 1);

                        ItemStack item = ItemStack.builder()
                                .itemTemplateId(template.getId())
                                .quantity(quantity)
                                .build();
                        droppedItems.add(item);
                    }
                    break;
                }
            }
        }
        return droppedItems;
    }
}
