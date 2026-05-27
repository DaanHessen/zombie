package dev.daanh.zombie.application.dto.response;

import java.util.List;
import java.util.UUID;

public record SurvivorDilemmaResponse(
    UUID survivorId,
    String survivorName,
    String dilemmaType,
    String description,
    List<DilemmaOption> options
) {
    public record DilemmaOption(
        String optionId,
        String text,
        String consequenceDescription
    ) {}
}
