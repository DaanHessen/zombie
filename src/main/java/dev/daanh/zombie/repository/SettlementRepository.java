package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.world.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@Repository
public class SettlementRepository {
    private static final String SQLITE_URL = "jdbc:sqlite:src/main/resources/data/sqlite/world_data.sqlite";

    private Settlement mapRow(ResultSet rs) throws java.sql.SQLException {
        return Settlement.builder()
                .id(UUID.fromString(rs.getString("id")))
                .geonameId(rs.getLong("geoname_id"))
                .name(rs.getString("name"))
                .coordinates(new dev.daanh.zombie.domain.world.Coordinates(rs.getDouble("latitude"), rs.getDouble("longitude")))
                .timezone(rs.getString("timezone"))
                .regionId(rs.getString("region_id") != null ? UUID.fromString(rs.getString("region_id")) : null)
                .language(rs.getString("language"))
                .preApocalypsePopulation(rs.getInt("population"))
                .groundZero(rs.getInt("is_ground_zero") != 0)
                .build();
    }

    public List<Settlement> findByNameIgnoreCase(String name) {
        List<Settlement> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM settlement WHERE LOWER(name) = LOWER(?)")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query settlement by name", e);
        }
        return results;
    }

    public List<Settlement> findAll() {
        List<Settlement> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM settlement");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query all settlements", e);
        }
        return results;
    }

    public List<Settlement> findByCoordinatesLatitudeBetweenAndCoordinatesLongitudeBetween(
            double minLat, double maxLat,
            double minLon, double maxLon
    ) {
        List<Settlement> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM settlement WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?")) {
            stmt.setDouble(1, minLat);
            stmt.setDouble(2, maxLat);
            stmt.setDouble(3, minLon);
            stmt.setDouble(4, maxLon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query settlements by bounding box", e);
        }
        return results;
    }

    public Optional<Settlement> findById(UUID id) {
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM settlement WHERE id = ?")) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query settlement by id", e);
        }
        return Optional.empty();
    }
}
