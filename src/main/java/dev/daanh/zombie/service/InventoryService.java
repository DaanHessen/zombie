package dev.daanh.zombie.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.item.ItemStack;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.enums.Freshness;
import dev.daanh.zombie.domain.item.records.SpoilageProfile;
import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.domain.person.enums.HobbyType;
import dev.daanh.zombie.domain.world.Country;
import dev.daanh.zombie.repository.InventoryRepository;
import dev.daanh.zombie.repository.ItemTemplateRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemTemplateRepository itemTemplateRepository;
    private final ItemRegistryService itemRegistryService;
    private final ObjectMapper objectMapper;

    private JsonNode startingLoadouts;

    @PostConstruct
    public void init() {
        try (InputStream is = new ClassPathResource("data/starting_loadouts.json").getInputStream()) {
            startingLoadouts = objectMapper.readTree(is);
            log.info("Successfully loaded starting_loadouts.json");
        } catch (Exception e) {
            log.error("Failed to load starting_loadouts.json", e);
        }
    }

    public void generateStartingInventory(Person survivor, Country country, long worldSeed) {
        if (survivor.getWorldContext().getInventory() == null) {
            survivor.getWorldContext().setInventory(new Inventory());
        }
        
        long seed = worldSeed;
        if (survivor.getId() != null) {
            seed += survivor.getId().hashCode();
        }
        Random random = new Random(seed);

        String occupation = survivor.getIdentity().getOccupation() != null ? survivor.getIdentity().getOccupation().name() : "UNEMPLOYED";
        
        JsonNode occupationLoadout = null;

        if (country != null) {
            if ("POLICE_OFFICER".equals(occupation) && country.getPoliceMilitarization() != null) {
                occupationLoadout = startingLoadouts.path("ARCHETYPES").path(country.getPoliceMilitarization().name()).path(occupation);
            } else if ("SOLDIER".equals(occupation) && country.getMilitaryArchetype() != null) {
                occupationLoadout = startingLoadouts.path("ARCHETYPES").path(country.getMilitaryArchetype().name()).path(occupation);
            } else if ("DOCTOR".equals(occupation) && country.getMedicalInfrastructure() != null) {
                occupationLoadout = startingLoadouts.path("ARCHETYPES").path("MEDICAL_INFRASTRUCTURE").path(country.getMedicalInfrastructure().name()).path(occupation);
            }
        }

        if (occupationLoadout == null || occupationLoadout.isMissingNode() || occupationLoadout.isNull()) {
            occupationLoadout = startingLoadouts.path("DEFAULT").path(occupation);
        }
        
        applyLoadout(survivor.getWorldContext().getInventory(), occupationLoadout, random);

        // Apply general civilian modifiers if not military/police
        if (country != null && !"POLICE_OFFICER".equals(occupation) && !"SOLDIER".equals(occupation)) {
            if (country.getCivilianFirearmOwnership() != null && !"NONE".equals(country.getCivilianFirearmOwnership().name())) {
                JsonNode firearmRoll = startingLoadouts.path("ARCHETYPES").path("CIVILIAN_FIREARMS").path(country.getCivilianFirearmOwnership().name());
                applyLoadout(survivor.getWorldContext().getInventory(), firearmRoll, random);
            }
        }

        // 2. Give Hobby Loadouts
        if (survivor.getIdentity().getHobbies() != null) {
            for (HobbyType hobby : survivor.getIdentity().getHobbies()) {
                JsonNode hobbyLoadout = startingLoadouts.path("GLOBAL_HOBBIES").path(hobby.name());
                applyLoadout(survivor.getWorldContext().getInventory(), hobbyLoadout, random);
            }
        }
        
        inventoryRepository.save(survivor.getWorldContext().getInventory());
    }

    private void applyLoadout(Inventory inventory, JsonNode loadoutArray, Random random) {
        if (loadoutArray.isMissingNode() || !loadoutArray.isArray()) return;

        for (JsonNode entry : loadoutArray) {
            int chance = entry.path("chance").asInt(100);
            if (random.nextInt(100) >= chance) {
                continue; // Failed the chance roll
            }

            int min = entry.path("min").asInt(1);
            int max = entry.path("max").asInt(1);
            int quantity = min + (max > min ? random.nextInt((max - min) + 1) : 0);

            if (quantity <= 0) continue;

            ItemTemplate templateToGive = null;

            if (entry.has("itemId")) {
                String itemIdStr = entry.get("itemId").asText();
                UUID itemId = generateDeterministicUuid(itemIdStr);
                templateToGive = itemRegistryService.getTemplate(itemId).orElse(null);
            } else if (entry.has("categoryId")) {
                String categoryId = entry.get("categoryId").asText();
                List<ItemTemplate> possibleItems = itemRegistryService.getAllTemplates().stream()
                        .filter(t -> t.getCategoryId() != null && t.getCategoryId().value().equalsIgnoreCase(categoryId))
                        .toList();
                
                if (!possibleItems.isEmpty()) {
                    templateToGive = possibleItems.get(random.nextInt(possibleItems.size()));
                }
            }

            if (templateToGive != null) {
                inventory.addItem(templateToGive, ItemStack.builder()
                        .itemTemplateId(templateToGive.getId())
                        .quantity(quantity)
                        .durability(100) // Fresh item
                        .build());
            }
        }
    }

    private UUID generateDeterministicUuid(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(input.getBytes());
        }
    }

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
