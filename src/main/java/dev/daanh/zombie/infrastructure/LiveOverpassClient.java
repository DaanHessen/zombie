package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.application.dto.response.OsmNodeResponse;
import dev.daanh.zombie.domain.world.Coordinates;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class LiveOverpassClient implements OsmClient {
    private final WebClient client;

    public LiveOverpassClient() {
        this.client = WebClient.create("https://overpass-api.de/api/interpreter");
    }

    @Override
    public List<OsmNodeResponse> fetchLocationsInBoundingBox(Coordinates min, Coordinates max) {

        String query = String.format(
                "[out:json][timeout:25];\n" +
                "(\n" +
                "  node[\"amenity\"=\"hospital\"](%f,%f,%f,%f);\n" +
                "  node[\"amenity\"=\"police\"](%f,%f,%f,%f);\n" +
                ");\n" +
                "out body;",
                min.getLatitude(), min.getLongitude(), max.getLatitude(), max.getLongitude(),
                min.getLatitude(), min.getLongitude(), max.getLatitude(), max.getLongitude()
        );

        OverpassResponse response = client.post()
                .bodyValue(query)
                .retrieve()
                .bodyToMono(OverpassResponse.class)
                .block();

        if (response == null || response.elements() == null) {
            return List.of();
        }

        return response.elements().stream()
                .filter(el -> el.tags() != null && el.tags().containsKey("name"))
                .map(el -> new OsmNodeResponse(
                        el.lat(),
                        el.lon(),
                        el.tags().get("name"),
                        el.tags()
                ))
                .toList();
    }

    public record OverpassResponse(List<OverpassElement> elements) {}
    public record OverpassElement(double lat, double lon, Map<String, String> tags) {}
}
