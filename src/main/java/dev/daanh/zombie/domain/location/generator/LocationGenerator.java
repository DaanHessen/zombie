package dev.daanh.zombie.domain.location.generator;

import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.world.Settlement;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LocationGenerator {

    public void generateProceduralHousing(Settlement settlement, dev.daanh.zombie.domain.world.chunks.ChunkCoordinates coords) {
        long amountOfLocations = calculateAmountOfLocations(settlement, new Random());

        for (int i = 0; i < amountOfLocations; i++) {
            // Wait, we need to create the housing here. The original didn't have implementation yet.
            // I'll leave the empty loop for now as it was empty before, just renamed the method.
        }
    }

    private int calculateAmountOfLocations(Settlement settlement, Random random) {
        int population = settlement.getPreApocalypsePopulation();
        int amount;

        if (population < 500) { amount = 1 + random.nextInt(10); }
        else if (population < 2500) { amount = 5 + random.nextInt(25); }
        else if (population < 5000) { amount = 10 + random.nextInt(40); }
        else if (population < 25000) { amount = 15 + random.nextInt(100); }
        else if (population < 100000) { amount = 50 + random.nextInt(250); }
        else if (population < 500000) { amount = 100 + random.nextInt(500); }
        else if (population < 1000000) { amount = 200 + random.nextInt(1000); }
        else { amount = 500 + random.nextInt(2000); }

        return amount;
    }
}
