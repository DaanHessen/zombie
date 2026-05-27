package dev.daanh.zombie.service.ai;

import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.domain.person.ai.Job;
import dev.daanh.zombie.domain.person.ai.JobType;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JobExecutorService {
    
    /**
     * Executes the person's current job for 1 tick.
     */
    public void executeJob(Person person) {
        Job currentJob = person.getBrain().getCurrentJob();
        
        if (currentJob == null) {
            return;
        }

        currentJob.setRemainingTicks(currentJob.getRemainingTicks() - 1);

        switch (currentJob.getType()) {
            case IDLE:
                // Do nothing
                break;
            case EAT:
                // Consume target item, increase hunger stats
                break;
            case SLEEP:
                // Increase energy
                break;
            case SCAVENGE:
                // Loot logic
                break;
            case WORK:
                // Base building/crafting logic
                break;
            case FIGHT:
                // Combat logic
                break;
            case FLEE:
                // Move away from threat
                break;
        }

        if (currentJob.getRemainingTicks() <= 0) {
            // Job completed, clear it so ThinkTree can assign a new one next tick
            person.getBrain().setCurrentJob(null);
        }
    }
}
