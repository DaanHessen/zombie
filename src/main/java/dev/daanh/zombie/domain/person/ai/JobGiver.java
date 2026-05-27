package dev.daanh.zombie.domain.person.ai;

import dev.daanh.zombie.domain.person.Person;

public abstract class JobGiver extends ThinkNode {

    @Override
    public Job tryIssueJob(Person person) {
        return tryGiveJob(person);
    }

    /**
     * The specific logic for a job giver (e.g. check hunger, find food, return EAT job).
     */
    protected abstract Job tryGiveJob(Person person);
}
