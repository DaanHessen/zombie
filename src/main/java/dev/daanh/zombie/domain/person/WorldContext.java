package dev.daanh.zombie.domain.person;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.person.enums.PersonStatus;
import dev.daanh.zombie.domain.world.Coordinates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WorldContext extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private PersonStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Embedded
    private Coordinates coordinates;
}
