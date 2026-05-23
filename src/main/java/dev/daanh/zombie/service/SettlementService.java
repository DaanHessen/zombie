package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.SettlementState;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.repository.SettlementRepository;
import dev.daanh.zombie.repository.SettlementStateRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class SettlementService {
    private final SettlementStateRepository settlementStateRepository;
    private final SettlementRepository settlementRepository;

    public void generateSettlementStates(World world) {
        List<Settlement> settlements = settlementRepository.findAll();

        for (Settlement settlement : settlements) {
            SettlementState settlementState = new SettlementState();
                    settlementState.setWorld(world);
                    settlementState.setSettlement(settlement);
                    settlementState.setPopulation(settlement.getPreApocalypsePopulation());
            settlementStateRepository.save(settlementState);
        }
    }
}
