package dev.daanh.zombie.domain.survivor;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class StatsProfile {
    private int intelligence;
    private int endurance;
    private int strength;
    private int agility;
}
