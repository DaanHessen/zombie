package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.service.ai.JobExecutorService;
import dev.daanh.zombie.service.ai.ThinkTreeService;
import dev.daanh.zombie.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PersonSimulationService {
    private final PersonRepository personRepository;
    private final NeedsDecayService needsDecayService;
    private final ThinkTreeService thinkTree;
    private final JobExecutorService jobExecutorService;

    /**
     * Called by the global simulation loop (which is currently paused for refactoring).
     */
    public void tickAllPersons() {
        List<Person> allPersons = personRepository.findAll();
        for (Person person : allPersons) {
            
            // 1. Apply biological decay (Hunger, Thirst, Energy, Health)
            needsDecayService.processTick(person);
            
            // 2. If the person has no job, evaluate the ThinkTree to find one
            thinkTree.evaluate(person);
            
            // 3. Execute the active job for 1 tick
            jobExecutorService.executeJob(person);
        }
    }
}
