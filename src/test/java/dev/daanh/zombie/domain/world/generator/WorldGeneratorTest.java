package dev.daanh.zombie.domain.world.generator;

import dev.daanh.zombie.domain.world.Continent;
import dev.daanh.zombie.domain.world.Country;
import dev.daanh.zombie.domain.world.Region;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldGeneratorTest {

    @Test
    void testGenerateWorld() {
        WorldGenerator generator = new WorldGenerator();
        World world = generator.generateWorld(10L);

        assertNotNull(world);
        assertEquals("Earth", world.getName());

        // Validate continents
        assertFalse(world.getContinents().isEmpty());
        // Standard continents should be 7 (Asia, Africa, North America, South America, Antarctica, Europe, Australia/Oceania)
        // Let's verify the actual count loaded from continents.json
        System.out.println("Loaded Continents count: " + world.getContinents().size());
        assertTrue(world.getContinents().size() >= 7);

        // Validate countries, regions, and settlements
        int totalCountries = 0;
        int totalRegions = 0;
        int totalSettlements = 0;

        for (Continent continent : world.getContinents()) {
            assertNotNull(continent.getWorld());
            assertEquals(world, continent.getWorld());

            for (Country country : continent.getCountries()) {
                assertNotNull(country.getContinent());
                assertEquals(continent, country.getContinent());
                totalCountries++;

                for (Region region : country.getRegions()) {
                    assertNotNull(region.getCountry());
                    assertEquals(country, region.getCountry());
                    totalRegions++;

                    for (Settlement settlement : region.getSettlements()) {
                        assertNotNull(settlement.getRegion());
                        assertEquals(region, settlement.getRegion());
                        totalSettlements++;
                    }
                }
            }
        }

        System.out.println("Total Countries loaded: " + totalCountries);
        System.out.println("Total Regions loaded: " + totalRegions);
        System.out.println("Total Settlements loaded: " + totalSettlements);
        System.out.println("Total Water Bodies loaded: " + world.getWaterBodies().size());

        assertTrue(totalCountries > 0, "No countries were loaded!");
        assertTrue(totalRegions > 0, "No regions were loaded!");
        assertTrue(totalSettlements > 0, "No settlements were loaded!");
        assertFalse(world.getWaterBodies().isEmpty(), "No water bodies were loaded!");

        // Validate that water bodies are bidirectionally linked to the world
        for (var wb : world.getWaterBodies()) {
            assertNotNull(wb.getWorld());
            assertEquals(world, wb.getWorld());
        }
    }
}
