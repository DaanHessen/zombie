package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.item.ItemStack;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.enums.Freshness;
import dev.daanh.zombie.domain.item.records.SpoilageProfile;
import dev.daanh.zombie.repository.InventoryRepository;
import dev.daanh.zombie.repository.ItemTemplateRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemTemplateRepository itemTemplateRepository;

    public List<ItemStack> openInventory(UUID inventoryId, long currentTick) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));

        if (inventory.getItems() == null) {
            return List.of();
        }

        boolean changed = false;

        for (ItemStack item : inventory.getItems()) {
            java.util.Map<String, Object> metadata = item.getMetadata();

            if (!metadata.containsKey("freshness") || Freshness.SPOILED.name().equals(metadata.get("freshness"))) {
                continue;
            }

            ItemTemplate template = itemTemplateRepository.findById(item.getItemTemplateId()).orElse(null);
            if (template == null) {
                continue;
            }

            SpoilageProfile spoilage = (SpoilageProfile) template.getProfiles().get(SpoilageProfile.class);
            if (spoilage == null) {
                continue;
            }

            long createdTick = ((Number) metadata.get("createdAtTick")).longValue();
            if (currentTick - createdTick >= spoilage.baseSpoilageTicks()) {
                metadata.put("freshness", Freshness.SPOILED.name());
                metadata.put("lastCheckedTick", currentTick);
                changed = true;
            }
        }

        if (changed) {
            inventoryRepository.save(inventory);
        }

        return inventory.getItems() != null ? inventory.getItems() : List.of();
    }

    public void transferItem(UUID sourceId, UUID targetId, UUID itemId, int quantity) {
        Inventory source = inventoryRepository.findById(sourceId).orElseThrow();
        Inventory target = inventoryRepository.findById(targetId).orElseThrow();

        if (source.getItems() == null || target.getItems() == null) {
            throw new IllegalStateException("Inventories must be initialized");
        }

        ItemStack itemToTransfer = source.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in source inventory"));

        if (quantity >= itemToTransfer.getQuantity()) {
            // Transfer entire stack
            source.getItems().remove(itemToTransfer);
            target.getItems().add(itemToTransfer);
        } else {
            // Split stack
            itemToTransfer.setQuantity(itemToTransfer.getQuantity() - quantity);

            ItemStack newStack = ItemStack.builder()
                    .itemTemplateId(itemToTransfer.getItemTemplateId())
                    .quantity(quantity)
                    .durability(itemToTransfer.getDurability())
                    .build();
            newStack.getMetadata().putAll(itemToTransfer.getMetadata());
            target.getItems().add(newStack);
        }

        inventoryRepository.save(source);
        inventoryRepository.save(target);
    }

    public void consumeItem(UUID inventoryId, UUID itemId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow();
        
        if (inventory.getItems() == null) return;

        ItemStack itemToConsume = inventory.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (quantity >= itemToConsume.getQuantity()) {
            inventory.getItems().remove(itemToConsume);
        } else {
            itemToConsume.setQuantity(itemToConsume.getQuantity() - quantity);
        }

        inventoryRepository.save(inventory);
    }
}
