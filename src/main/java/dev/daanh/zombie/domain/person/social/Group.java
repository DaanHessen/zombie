package dev.daanh.zombie.domain.person.social;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.person.enums.GovernmentType;
import dev.daanh.zombie.domain.person.Person;
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
    private List<Person> survivors;

    @OneToMany
    @JoinTable(
        name = "group_leaders",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "leader_id")
    )
    private List<Person> leaders;

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
