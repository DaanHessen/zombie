package dev.daanh.zombie.domain.world.generator;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.repository.WorldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

@Service
@Slf4j
public class WorldGenerator {
    public void seedWorld(Connection conn, UUID worldId) throws Exception {
        seedTable(conn, "continent", "id, name, code", "/data/continents.csv.gz");
        seedTable(conn, "country", "id, name, code, capital, population, square_kilometers, continent_id", "/data/countries.csv.gz");
        seedTable(conn, "region", "id, name, code, country_id", "/data/regions.csv.gz");
        seedTable(conn, "settlement", "id, geoname_id, name, latitude, longitude, population, timezone, biome, region_id, language, is_ground_zero", "/data/settlements.csv.gz");
        seedTable(conn, "water_body", "id, name, type", "/data/oceans_seas.csv.gz");
//        seedTable(conn, "road_nodes", "id, latitude, longitude, settlement_id", "/data/road_nodes.csv.gz");
//        seedTable(conn, "road_segments", "id, name, start_node_id, end_node_id, type, blocked, settlement_id", "/data/road_segments.csv.gz");
//        seedTable(conn, "special_locations", "id, name, type, latitude, longitude, looted, settlement_id", "/data/special_locations.csv.gz");
    }

    private void seedTable(Connection conn, String tableName, String columns, String resourcePath) throws Exception {
        CopyManager copyManager = new CopyManager(conn.unwrap(BaseConnection.class));

        try (InputStream is = WorldGenerator.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.error("Resource not found: " + resourcePath);
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            GZIPInputStream csvStream = new GZIPInputStream(is);

            String sql = String.format("COPY %s (%s) FROM STDIN WITH (FORMAT CSV, HEADER)", tableName, columns);
            copyManager.copyIn(sql, csvStream);
        }
    }
}