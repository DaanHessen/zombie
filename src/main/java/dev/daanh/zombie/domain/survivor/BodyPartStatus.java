package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.survivor.enums.BodyPartType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BodyPartStatus {
    @Enumerated(EnumType.STRING)
    private BodyPartType type;
    private int health;
    private int pain;
    private int bleeding;
    private boolean disabled;
}
