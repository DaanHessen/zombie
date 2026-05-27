package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.survivor.enums.BodyPartType;
import dev.daanh.zombie.domain.survivor.enums.InfectionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Infection extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private InfectionType type;
    private int severity;
    private int duration;
    private boolean curable;

    @Enumerated(EnumType.STRING)
    private BodyPartType bodyPart;

    public void decreaseDuration(int amount) {
        this.duration = Math.max(0, this.duration - amount);
    }
}
