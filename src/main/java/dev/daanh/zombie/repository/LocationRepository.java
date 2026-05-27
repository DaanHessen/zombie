package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;

import org.springframework.stereotype.Repository;
import dev.daanh.zombie.infrastructure.OsmLocationFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@Repository
public class LocationRepository {
    private static final String SQLITE_URL = "jdbc:sqlite:src/main/resources/data/sqlite/world_data.sqlite";

    private final OsmLocationFactory osmLocationFactory;

    public LocationRepository(OsmLocationFactory osmLocationFactory) {
        this.osmLocationFactory = osmLocationFactory;
    }

    public List<Location> findByBoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
        List<Location> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(SQLITE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pois WHERE lat BETWEEN ? AND ? AND lon BETWEEN ? AND ?")) {
            stmt.setDouble(1, minLat);
            stmt.setDouble(2, maxLat);
            stmt.setDouble(3, minLon);
            stmt.setDouble(4, maxLon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Location loc = osmLocationFactory.createLocation(
                        rs.getDouble("lat"), rs.getDouble("lon"),
                        rs.getString("name"), rs.getString("category"),
                        rs.getDouble("area_sqm")
                    );
                    loc.setId(UUID.fromString(rs.getString("id")));
                    results.add(loc);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query locations by bounding box", e);
        }
        return results;
    }
}
