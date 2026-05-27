package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.domain.person.mind.NeedsProfile;
import dev.daanh.zombie.domain.person.health.HealthProfile;
import org.springframework.stereotype.Service;

@Service
public class NeedsDecayService {

    public void processTick(Person survivor) {
        NeedsProfile needs = survivor.getBrain().getNeeds();
        HealthProfile health = survivor.getBody().getHealth();
        
        // Base decay rates per hour
        double hungerDecay = 1.0;
        double thirstDecay = 2.0;
        double energyDecay = 3.0;
        double sanityDecay = 0.0;

        // Apply decay
        needs.setHunger(Math.min(100, needs.getHunger() + (int) hungerDecay));
        needs.setThirst(Math.min(100, needs.getThirst() + (int) thirstDecay));
        needs.setEnergy(Math.max(0, needs.getEnergy() - (int) energyDecay));
        health.setSanity(Math.max(0, health.getSanity() - (int) sanityDecay));

        // Health consequences of neglected needs
        if (needs.getHunger() >= 100) {
            health.setHealth(Math.max(0, health.getHealth() - 2));
        } else if (needs.getHunger() >= 80) {
            health.setHealth(Math.max(0, health.getHealth() - 1));
        }

        if (needs.getThirst() >= 100) {
            health.setHealth(Math.max(0, health.getHealth() - 5));
        } else if (needs.getThirst() >= 80) {
            health.setHealth(Math.max(0, health.getHealth() - 2));
        }

        if (needs.getEnergy() <= 0) {
            health.setSanity(Math.max(0, health.getSanity() - 5));
            health.setHealth(Math.max(0, health.getHealth() - 1));
        }

        // Sanity consequences
        if (health.getSanity() <= 0) {
            // Mental break logic handled by AI
        }
    }
}
