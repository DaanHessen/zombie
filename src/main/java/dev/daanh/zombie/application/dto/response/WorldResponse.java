package dev.daanh.zombie.application.dto.response;

import dev.daanh.zombie.domain.world.World;

import java.util.UUID;

public record WorldResponse(
        UUID id,
        String name,
        long continentCount,
        long waterBodyCount,
        long countryCount,
        long regionCount,
        long settlementCount,
        long generationTimeMs
) {
    public static WorldResponse from(World world, WorldStatsResponse stats, long generationTimeMs) {
        return new WorldResponse(
                world.getId(),
                world.getName(),
                stats.continents(),
                stats.waterBodies(),
                stats.countries(),
                stats.regions(),
                stats.settlements(),
                generationTimeMs
        );
    }
}
