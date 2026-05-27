package dev.daanh.zombie.domain.person.social;

import dev.daanh.zombie.domain.core.BaseEntity;

import dev.daanh.zombie.domain.person.Person;
import dev.daanh.zombie.domain.person.enums.RelationshipLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Relationship extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private RelationshipLabel label;

    private int trustScore; // 0 to 100
    private int romanticInterest; // 0 to 100

    @ManyToOne
    private Person targetPerson;
}
