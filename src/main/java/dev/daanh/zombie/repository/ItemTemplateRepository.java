package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.ItemProfile;
import dev.daanh.zombie.domain.item.records.ItemCategoryId;
import dev.daanh.zombie.domain.item.records.ItemGroupId;
import dev.daanh.zombie.domain.item.records.NutritionProfile;
import dev.daanh.zombie.domain.item.records.SpoilageProfile;
import dev.daanh.zombie.domain.item.records.WeaponProfile;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemTemplateRepository {

    private static final String SQLITE_URL = "jdbc:sqlite:data/sqlite/world_data.sqlite";

    private static ItemTemplate mapRow(ResultSet rs) throws SQLException {
        Map<Class<? extends ItemProfile>, ItemProfile> profiles = new HashMap<>();

        long spoilageTicks = rs.getLong("base_spoilage_ticks");
        if (spoilageTicks > 0) {
            profiles.put(SpoilageProfile.class, new SpoilageProfile(spoilageTicks));
        }

        int calories = rs.getInt("calories");
        int protein = rs.getInt("protein_grams");
        int carbs = rs.getInt("carbs_grams");
        int hydration = rs.getInt("hydration_value");
        if (calories > 0 || protein > 0 || carbs > 0 || hydration > 0) {
            profiles.put(NutritionProfile.class, new NutritionProfile(calories, protein, carbs, hydration));
        }

        double baseDamage = rs.getDouble("base_damage");
        double durabilityLoss = rs.getDouble("durability_loss_per_use");
        double noiseRadius = rs.getDouble("noise_radius_meters");
        if (baseDamage > 0 || durabilityLoss > 0 || noiseRadius > 0) {
            profiles.put(WeaponProfile.class, new WeaponProfile(baseDamage, durabilityLoss, noiseRadius));
        }

        return ItemTemplate.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .categoryId(new ItemCategoryId(rs.getString("type")))
                .weightGrams((int) rs.getDouble("weight_grams"))
                .maxStackSize(rs.getInt("max_stack_size"))
                .volumeLitres(rs.getDouble("volume_litres"))
                .instanceTracked(rs.getInt("instance_tracked") != 0)
                .profiles(profiles)
                .build();
    }

    public Optional<ItemTemplate> findById(UUID id) {
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_templates WHERE id = ?")) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query item template by id", e);
        }
        return Optional.empty();
    }

    public List<ItemTemplate> findAll() {
        List<ItemTemplate> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_templates");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query all item templates", e);
        }
        return results;
    }

    public List<ItemTemplate> findByCategory(ItemGroupId categoryId) {
        List<ItemTemplate> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_templates WHERE UPPER(category) = UPPER(?)")) {
            stmt.setString(1, categoryId.value());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query item templates by category", e);
        }
        return results;
    }

    public List<ItemTemplate> findByType(ItemCategoryId typeId) {
        List<ItemTemplate> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_templates WHERE UPPER(type) = UPPER(?)")) {
            stmt.setString(1, typeId.value());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query item templates by type", e);
        }
        return results;
    }

    public List<ItemTemplate> findPerishableFoods() {
        List<ItemTemplate> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_templates WHERE UPPER(category) = 'FOOD' AND base_spoilage_ticks > 0");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query perishable foods", e);
        }
        return results;
    }
}
