package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.survivor.enums.GovernmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "survivor_group")
public class Group extends BaseEntity {
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Survivor> survivors;

    @OneToMany
    @JoinTable(
        name = "group_leaders",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "leader_id")
    )
    private List<Survivor> leaders;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    private GovernmentType governmentType;

    private int cohesion;

    public void increaseCohesion(int amount) {
        this.cohesion = Math.min(100, this.cohesion + amount);
    }

    public void decreaseCohesion(int amount) {
        this.cohesion = Math.max(0, this.cohesion - amount);
    }
}
