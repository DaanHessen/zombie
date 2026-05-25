package dev.daanh.zombie.infrastructure;

import dev.daanh.zombie.application.dto.response.OsmNodeResponse;
import dev.daanh.zombie.domain.world.Coordinates;

import java.util.List;

public interface OsmClient {
    List<OsmNodeResponse> fetchLocationsInBoundingBox(Coordinates min, Coordinates max);
}
