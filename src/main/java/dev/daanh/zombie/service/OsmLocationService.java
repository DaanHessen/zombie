package dev.daanh.zombie.service;

import dev.daanh.zombie.application.dto.response.OsmNodeResponse;
import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.chunks.ChunkCoordinates;
import dev.daanh.zombie.infrastructure.OsmClient;
import dev.daanh.zombie.infrastructure.OsmTagMapper;
import dev.daanh.zombie.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OsmLocationService {
    private final OsmClient osmClient;
    private final OsmTagMapper osmTagMapper;
    private final LocationRepository locationRepository;

    @Transactional
    public void seedPoisForChunk(ChunkCoordinates chunkCoords, double chunkSizeKm, Settlement settlement) {
        Coordinates min = getMinCoordinates(chunkCoords, chunkSizeKm);
        Coordinates max = getMaxCoordinates(chunkCoords, chunkSizeKm);

        try {
            List<OsmNodeResponse> osmLocations = osmClient.fetchLocationsInBoundingBox(min, max);
            
            for (OsmNodeResponse node : osmLocations) {
                OsmTagMapper.LocationMetadata meta = osmTagMapper.resolve(node.tags());

                Location realLocation = Location.builder()
                        .name(node.name())
                        .coordinates(new Coordinates(node.latitude(), node.longitude()))
                        .type(meta.type())
                        .category(meta.category())
                        .size(meta.size())
                        .generated(true)
                        .indoors(true) // TODO: find a way to determine if location is indoors
                        .region(settlement.getRegion())
                        .settlement(settlement)
                        .build();

                locationRepository.save(realLocation);
            }
            
            log.info("Successfully seeded {} real-world POIs from OSM.", osmLocations.size());
        } catch (Exception e) {
            log.warn("Offline or rate-limited ({}). Skipping real-world POIs for this chunk.", e.getMessage());
        }
    }

    private Coordinates getMinCoordinates(ChunkCoordinates chunkCoords, double chunkSizeKm) {
        return new Coordinates( (chunkCoords.getZ() * chunkSizeKm) / 111.0, (chunkCoords.getX() * chunkSizeKm) / 111.0 );
    }

    private Coordinates getMaxCoordinates(ChunkCoordinates chunkCoords, double chunkSizeKm) {
        return new Coordinates( ((chunkCoords.getZ() + 1) * chunkSizeKm) / 111.0, ((chunkCoords.getX() + 1) * chunkSizeKm) / 111.0 );
    }
}
