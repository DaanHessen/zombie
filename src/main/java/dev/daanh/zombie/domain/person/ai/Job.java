package dev.daanh.zombie.domain.person.ai;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Job {
    @Id
    private UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    private JobType type;

    // Optional targets (like an Item ID, Person ID, or Coordinates ID)
    private UUID targetId;
    
    // Ticks remaining until the job is completed
    private int remainingTicks;

    public Job() {}

    public Job(JobType type, int durationTicks) {
        this.type = type;
        this.remainingTicks = durationTicks;
    }

    public Job(JobType type, UUID targetId, int durationTicks) {
        this.type = type;
        this.targetId = targetId;
        this.remainingTicks = durationTicks;
    }
}
