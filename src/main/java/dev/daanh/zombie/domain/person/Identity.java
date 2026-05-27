package dev.daanh.zombie.domain.person;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.person.identity.Skills;
import dev.daanh.zombie.domain.person.enums.GenderType;
import dev.daanh.zombie.domain.person.enums.HobbyType;
import dev.daanh.zombie.domain.person.enums.OccupationType;
import dev.daanh.zombie.domain.person.enums.TraitType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Identity extends BaseEntity {
    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private OccupationType occupation;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Embedded
    private Skills skills = new Skills();

    @ElementCollection(targetClass = TraitType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "identity_traits", joinColumns = @JoinColumn(name = "identity_id"))
    private List<TraitType> traits = new ArrayList<>();

    @ElementCollection(targetClass = HobbyType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "identity_hobbies", joinColumns = @JoinColumn(name = "identity_id"))
    private List<HobbyType> hobbies = new ArrayList<>();

    private int daysInApocalypse;
}
