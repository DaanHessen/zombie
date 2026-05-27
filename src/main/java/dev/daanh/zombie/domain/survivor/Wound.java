package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.survivor.enums.BodyPartType;
import dev.daanh.zombie.domain.survivor.enums.WoundType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Wound extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private WoundType type;
    private int severity;
    private int duration;
    private int bloodLossPerTick;
    private boolean isBandaged;
    private boolean isTreated;
    private int infectionRisk;

    @Enumerated(EnumType.STRING)
    private BodyPartType bodyPart;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wound_id")
    private List<Infection> infections;
}
