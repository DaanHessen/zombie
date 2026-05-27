package dev.daanh.zombie.domain.person.anatomy;

import dev.daanh.zombie.domain.core.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class NervousSystem extends BaseEntity {
    private int painLevel = 0;
    private boolean inShock = false;
    private int panicLevel = 0;
}
