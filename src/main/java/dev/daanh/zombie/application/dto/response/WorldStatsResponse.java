package dev.daanh.zombie.application.dto.response;

public record WorldStatsResponse(
        long continents,
        long countries,
        long regions,
        long settlements,
        long waterBodies
) {}
