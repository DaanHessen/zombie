package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.item.ItemStack;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.enums.Freshness;
import dev.daanh.zombie.domain.item.records.SpoilageProfile;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.repository.ChunkRepository;
import dev.daanh.zombie.repository.InventoryRepository;
import dev.daanh.zombie.repository.ItemTemplateRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ItemService {

    private final InventoryRepository inventoryRepository;
    private final ChunkRepository chunkRepository;
    private final ItemTemplateRepository itemTemplateRepository;

    public ItemStack spawnInChunk(UUID templateId, UUID chunkId, int quantity, long currentTick) {
        ItemTemplate template = getTemplate(templateId);
        Chunk chunk = chunkRepository.findById(chunkId)
                .orElseThrow(() -> new IllegalArgumentException("Chunk not found"));

        if (chunk.getItems() == null) chunk.setItems(new java.util.ArrayList<>());

        ItemStack item = ItemStack.builder()
                .itemTemplateId(templateId)
                .quantity(Math.min(quantity, template.getMaxStackSize()))
                .build();

        attachMetadataIfNeeded(item, template, currentTick);
        chunk.getItems().add(item);
        chunkRepository.save(chunk);

        log.debug("Spawned {} x '{}' on floor in chunk {}", item.getQuantity(), template.getName(), chunkId);
        return item;
    }

    public ItemStack spawnInInventory(UUID templateId, UUID inventoryId, int quantity, long currentTick) {
        ItemTemplate template = getTemplate(templateId);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        if (inventory.getItems() == null) inventory.setItems(new java.util.ArrayList<>());

        ItemStack item = ItemStack.builder()
                .itemTemplateId(templateId)
                .quantity(Math.min(quantity, template.getMaxStackSize()))
                .build();

        attachMetadataIfNeeded(item, template, currentTick);
        inventory.getItems().add(item);
        inventoryRepository.save(inventory);

        log.debug("Spawned {} x '{}' in inventory {}", item.getQuantity(), template.getName(), inventoryId);
        return item;
    }

    private ItemTemplate getTemplate(UUID templateId) {
        return itemTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("No item template found for id: " + templateId));
    }

    private void attachMetadataIfNeeded(ItemStack item, ItemTemplate template, long currentTick) {
        SpoilageProfile spoilage = (SpoilageProfile) template.getProfiles().get(SpoilageProfile.class);
        if (spoilage != null && spoilage.baseSpoilageTicks() > 0) {
            item.getMetadata().put("freshness", Freshness.FRESH.name());
            item.getMetadata().put("createdAtTick", currentTick);
            item.getMetadata().put("lastCheckedTick", currentTick);
        }
    }
}
