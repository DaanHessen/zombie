package dev.daanh.zombie.domain.survivor;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ActiveTask {
    private String taskName;
    private int remainingTicks;
    private UUID targetId;
}
