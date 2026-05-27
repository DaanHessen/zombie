package dev.daanh.zombie.service.ai;

import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.domain.person.ai.Job;
import dev.daanh.zombie.domain.person.ai.JobGiver_Idle;
import dev.daanh.zombie.domain.person.ai.ThinkNode;
import dev.daanh.zombie.domain.person.ai.ThinkNode_Priority;
import org.springframework.stereotype.Service;

@Service
public class ThinkTreeService {
    private final ThinkNode rootNode;

    public ThinkTreeService() {
        // Construct the global AI tree
        ThinkNode_Priority root = new ThinkNode_Priority();
        
        // Lowest priority: IDLE
        root.addSubNode(new JobGiver_Idle());
        
        this.rootNode = root;
    }

    /**
     * Evaluates the tree and assigns a new Job if the person is currently idle.
     */
    public void evaluate(Person person) {
        if (person.getBrain().getCurrentJob() == null || person.getBrain().getCurrentJob().getRemainingTicks() <= 0) {
            Job newJob = rootNode.tryIssueJob(person);
            person.getBrain().setCurrentJob(newJob);
        }
    }
}
