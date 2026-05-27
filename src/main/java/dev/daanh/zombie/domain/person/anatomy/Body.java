package dev.daanh.zombie.domain.person.anatomy;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.person.health.StatsProfile;
import dev.daanh.zombie.domain.person.health.HealthProfile;
import dev.daanh.zombie.domain.person.health.Wound;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Body extends BaseEntity {
    @Embedded
    private StatsProfile stats = new StatsProfile();

    @Embedded
    private HealthProfile health = new HealthProfile();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "body_id")
    private List<Wound> wounds = new ArrayList<>();
}
