package dev.daanh.zombie.domain.person.anatomy;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.person.health.Infection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ImmuneSystem extends BaseEntity {
    private int immunityStrength = 100;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "immune_system_id")
    private List<Infection> infections = new ArrayList<>();
}
