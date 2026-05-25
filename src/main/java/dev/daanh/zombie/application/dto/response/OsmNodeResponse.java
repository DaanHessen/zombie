package dev.daanh.zombie.application.dto.response;

import java.util.Map;

public record OsmNodeResponse(
        double latitude,
        double longitude,
        String name,
        Map<String, String> tags
) {
}
