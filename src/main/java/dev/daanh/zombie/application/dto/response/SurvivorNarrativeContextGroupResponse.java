package dev.daanh.zombie.application.dto.response;

import java.util.List;
import java.util.UUID;

public record SurvivorNarrativeContextGroupResponse(
    UUID groupId,
    int daysInApocalypse,
    List<SurvivorNarrativeState> survivors
) {
    public record SurvivorNarrativeState(
        UUID survivorId,
        String name,
        int health,
        int mood,
        int morale,
        int sanity,
        int hunger,
        int hydration,
        int protein,
        int carbs,
        List<String> traits,
        List<String> conditions,
        String thought
    ) {}
}
