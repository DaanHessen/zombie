package dev.daanh.zombie.domain.person.ai;

import dev.daanh.zombie.domain.person.Person;

public class ThinkNode_Priority extends ThinkNode {

    @Override
    public Job tryIssueJob(Person person) {
        for (ThinkNode node : subNodes) {
            Job job = node.tryIssueJob(person);
            if (job != null) {
                return job;
            }
        }
        return null;
    }
}
