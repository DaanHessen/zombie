package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OsmLocationService {
    private final LocationRepository locationRepository;

    @Transactional
    public void seedPoisForSettlement(Settlement settlement) {
        if (settlement.getCoordinates() == null) return;
        
        // TODO: Implement reading from the offline .csv.gz data here
        log.info("Offline POI seeding for settlement {} will be implemented here.", settlement.getName());
    }
}
