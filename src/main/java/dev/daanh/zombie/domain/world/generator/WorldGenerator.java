package dev.daanh.zombie.domain.world.generator;

import dev.daanh.zombie.domain.world.Continent;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.Country;
import dev.daanh.zombie.domain.world.Region;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.WaterBody;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.enums.BiomeType;
import dev.daanh.zombie.domain.world.enums.WaterBodyType;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldGenerator {
    public World generateWorld() {
        World world = new World();
        world.setName("Earth");

        Map<String, Continent> continents = loadContinents(world);
        world.getContinents().addAll(continents.values());

        Map<String, Country> countries = loadCountries(continents);

        Map<String, Region> regions = loadRegions(countries);

        loadSettlements(regions);

        List<WaterBody> waterBodies = loadWaterBodies(world);
        world.getWaterBodies().addAll(waterBodies);

        return world;
    }

    private Map<String, Continent> loadContinents(World world) {
        Map<String, Continent> continents = new HashMap<>();

        try (InputStream is = getClass().getResourceAsStream("/data/continents.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(is);

            for (JsonNode continentNode : jsonNode) {
                String code = continentNode.get("code").asText();
                String name = continentNode.get("name").asText();

                Continent continent = Continent.builder()
                        .name(name)
                        .code(code)
                        .world(world)
                        .countries(new ArrayList<>())
                        .build();

                continents.put(code, continent);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to load continents: " + e);
        }

        return continents;
    }

    private Map<String, Country> loadCountries(Map<String, Continent> continents) {
        Map<String, Country> countries = new HashMap<>();

        try (InputStream is = getClass().getResourceAsStream("/data/countries.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(is);

            for (JsonNode countryNode : jsonNode) {
                String name = countryNode.get("name").asText();
                String code = countryNode.get("iso").asText();
                String capital = countryNode.get("capital").asText();
                int population = countryNode.get("population").asInt();
                double squareKilometers = countryNode.get("areaSqKm").asDouble();
                String continentCode = countryNode.get("continent").asText();

                Continent continent = continents.get(continentCode);

                Country country = Country.builder()
                        .name(name)
                        .code(code)
                        .capital(capital)
                        .population(population)
                        .squareKilometers(squareKilometers)
                        .regions(new ArrayList<>())
                        .continent(continent)
                        .build();

                if (continent != null) {
                    continent.getCountries().add(country);
                }

                countries.put(code, country);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to load countries: " + e);
        }

        return countries;
    }

    private Map<String, Region> loadRegions(Map<String, Country> countries) {
        Map<String, Region> regions = new HashMap<>();

        try (InputStream is = getClass().getResourceAsStream("/data/regions.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(is);

            for (JsonNode regionNode : jsonNode) {
                String name = regionNode.get("name").asText();
                String regionCode = regionNode.get("regionCode").asText();
                String countryCode = regionNode.get("countryCode").asText();

                Country country = countries.get(countryCode);

                Region region = Region.builder()
                        .name(name)
                        .code(regionCode)
                        .country(country)
                        .settlements(new ArrayList<>())
                        .build();

                if (country != null) {
                    country.getRegions().add(region);
                }

                String compositeKey = countryCode + "_" + regionCode;
                regions.put(compositeKey, region);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to load regions: " + e);
        }

        return regions;
    }

    private void loadSettlements(Map<String, Region> regions) {
        try (InputStream is = getClass().getResourceAsStream("/data/settlements.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(is);

            for (JsonNode settlementNode : jsonNode) {
                Long geonameId = settlementNode.get("geonameId").asLong();
                String name = settlementNode.get("name").asText();
                Double lat = settlementNode.get("latitude").asDouble();
                Double lon = settlementNode.get("longitude").asDouble();
                int population = settlementNode.get("population").asInt();
                String timezone = settlementNode.get("timezone").asText();
                String countryCode = settlementNode.get("countryCode").asText();
                String regionCode = settlementNode.get("regionCode").asText();
                String biomeName = settlementNode.get("biome").asText();

                String compositeKey = countryCode + "_" + regionCode;
                Region region = regions.get(compositeKey);

                Coordinates coordinates = Coordinates.builder()
                        .latitude(lat)
                        .longitude(lon)
                        .build();

                BiomeType biome = BiomeType.valueOf(biomeName.toUpperCase());

                Settlement settlement = Settlement.builder()
                        .geonameId(geonameId)
                        .name(name)
                        .coordinates(coordinates)
                        .population(population)
                        .timezone(timezone)
                        .biome(biome)
                        .region(region)
                        .build();

                if (region != null) {
                    region.getSettlements().add(settlement);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to load settlements: " + e);
        }
    }

    private List<WaterBody> loadWaterBodies(World world) {
        List<WaterBody> waterBodies = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/data/oceans_seas.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(is);

            for (JsonNode waterBodyNode : jsonNode) {
                String name = waterBodyNode.get("name").asText();
                WaterBodyType type = WaterBodyType.valueOf(waterBodyNode.get("type").asText().toUpperCase());

                WaterBody waterBody = WaterBody.builder()
                        .name(name)
                        .type(type)
                        .world(world)
                        .build();

                waterBodies.add(waterBody);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to load water bodies: " + e);
        }

        return waterBodies;
    }
}