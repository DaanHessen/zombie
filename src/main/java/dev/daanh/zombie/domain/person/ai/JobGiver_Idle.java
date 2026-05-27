package dev.daanh.zombie.domain.person.ai;

import dev.daanh.zombie.domain.person.Person;

public class JobGiver_Idle extends JobGiver {
    @Override
    protected Job tryGiveJob(Person person) {
        return new Job(JobType.IDLE, 1);
    }
}
