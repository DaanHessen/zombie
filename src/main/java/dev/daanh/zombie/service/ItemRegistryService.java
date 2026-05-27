package dev.daanh.zombie.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import dev.daanh.zombie.domain.item.ItemProfile;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.profiles.*;
import dev.daanh.zombie.domain.item.records.ItemCategoryId;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRegistryService {

    private final ObjectMapper objectMapper;
    private final Map<UUID, ItemTemplate> registry = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Loading Item Registry from JSON...");
        try (InputStream is = new ClassPathResource("data/items.json").getInputStream()) {
            JsonNode rootArray = objectMapper.readTree(is);

            for (JsonNode node : rootArray) {
                String idStr = node.get("id").asText();
                // If the CSV generated IDs like "item_weapon_...", we hash it to a consistent UUID for the registry.
                // If it's already a UUID string, this just parses it.
                UUID id = generateDeterministicUuid(idStr);

                ItemTemplate template = ItemTemplate.builder()
                        .id(id)
                        .name(node.get("name").asText())
                        .description(node.path("description").asText(""))
                        .categoryId(new ItemCategoryId(node.path("categoryId").asText("UNKNOWN")))
                        .weightGrams(node.path("weightGrams").asInt(0))
                        .maxStackSize(node.path("maxStackSize").asInt(1))
                        .instanceTracked(node.path("instanceTracked").asBoolean(false))
                        .volumeLitres(node.path("volumeLitres").asDouble(0.0))
                        .profiles(parseProfiles(node.path("profiles")))
                        .build();

                registry.put(id, template);
            }
            log.info("Successfully loaded {} items into the registry.", registry.size());

        } catch (Exception e) {
            log.error("Failed to load items.json into ItemRegistryService!", e);
        }
    }

    public Optional<ItemTemplate> getTemplate(UUID id) {
        return Optional.ofNullable(registry.get(id));
    }

    public List<ItemTemplate> getAllTemplates() {
        return new ArrayList<>(registry.values());
    }

    private Map<Class<? extends ItemProfile>, ItemProfile> parseProfiles(JsonNode profilesNode) {
        Map<Class<? extends ItemProfile>, ItemProfile> map = new HashMap<>();
        if (profilesNode.isMissingNode() || profilesNode.isNull()) {
            return map;
        }

        if (profilesNode.has("WeaponProfile")) {
            WeaponProfile p = objectMapper.convertValue(profilesNode.get("WeaponProfile"), WeaponProfile.class);
            map.put(WeaponProfile.class, p);
        }
        if (profilesNode.has("ConsumableProfile")) {
            ConsumableProfile p = objectMapper.convertValue(profilesNode.get("ConsumableProfile"), ConsumableProfile.class);
            map.put(ConsumableProfile.class, p);
        }
        if (profilesNode.has("ClothingProfile")) {
            ClothingProfile p = objectMapper.convertValue(profilesNode.get("ClothingProfile"), ClothingProfile.class);
            map.put(ClothingProfile.class, p);
        }
        if (profilesNode.has("MedicalProfile")) {
            MedicalProfile p = objectMapper.convertValue(profilesNode.get("MedicalProfile"), MedicalProfile.class);
            map.put(MedicalProfile.class, p);
        }
        if (profilesNode.has("ToolProfile")) {
            ToolProfile p = objectMapper.convertValue(profilesNode.get("ToolProfile"), ToolProfile.class);
            map.put(ToolProfile.class, p);
        }
        if (profilesNode.has("AmmunitionProfile")) {
            AmmunitionProfile p = objectMapper.convertValue(profilesNode.get("AmmunitionProfile"), AmmunitionProfile.class);
            map.put(AmmunitionProfile.class, p);
        }
        if (profilesNode.has("ContainerProfile")) {
            ContainerProfile p = objectMapper.convertValue(profilesNode.get("ContainerProfile"), ContainerProfile.class);
            map.put(ContainerProfile.class, p);
        }
        if (profilesNode.has("LiteratureProfile")) {
            LiteratureProfile p = objectMapper.convertValue(profilesNode.get("LiteratureProfile"), LiteratureProfile.class);
            map.put(LiteratureProfile.class, p);
        }

        return map;
    }

    private UUID generateDeterministicUuid(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(input.getBytes());
        }
    }
}
