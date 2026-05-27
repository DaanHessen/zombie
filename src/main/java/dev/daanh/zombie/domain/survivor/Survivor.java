package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.survivor.enums.*;
import dev.daanh.zombie.domain.world.Coordinates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Survivor extends BaseEntity {
    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private OccupationType occupation;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    private SurvivorStatus status;

    @Embedded
    private SkillsProfile skills;

    @Embedded
    private StatsProfile stats;

    @Embedded
    private HealthProfile health;

    @Embedded
    private NeedsProfile needs = new NeedsProfile();

    @Embedded
    private KnowledgeProfile knowledge;

    @Embedded
    private ActiveTask activeTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id")
    @JoinColumn(name = "survivor_owner_id")
    private Map<UUID, Relationship> relationships;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Embedded
    private Coordinates coordinates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survivor_id")
    private List<Infection> infections;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survivor_id")
    private List<Wound> wounds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survivor_id")
    private List<Condition> conditions;

    @ElementCollection(targetClass = TraitType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "survivor_traits", joinColumns = @JoinColumn(name = "survivor_id"))
    private List<TraitType> traits;

    private int daysInApocalypse;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "survivor_id")
    private List<Memory> memories;

    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;

    private int influence;
}
