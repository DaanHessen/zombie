package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.survivor.enums.ConditionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Condition extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ConditionType type;
    private int severity;
    private int duration;
    private boolean curable;

    public void decreaseDuration(int amount) {
        this.duration = Math.max(0, this.duration - amount);
    }
}
