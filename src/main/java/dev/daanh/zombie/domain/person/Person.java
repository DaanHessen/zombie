package dev.daanh.zombie.domain.person;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.item.Inventory;
import dev.daanh.zombie.domain.person.anatomy.*;
import dev.daanh.zombie.domain.person.enums.*;
import dev.daanh.zombie.domain.person.social.Group;
import dev.daanh.zombie.domain.person.social.Relationship;
import dev.daanh.zombie.domain.person.identity.Skills;
import dev.daanh.zombie.domain.world.Coordinates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Person extends BaseEntity {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "brain_id")
    private Brain brain = new Brain();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "body_id")
    private Body body = new Body();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nervous_system_id")
    private NervousSystem nervousSystem = new NervousSystem();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "immune_system_id")
    private ImmuneSystem immuneSystem = new ImmuneSystem();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "identity_id")
    private Identity identity = new Identity();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "social_context_id")
    private SocialContext socialContext = new SocialContext();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "world_context_id")
    private WorldContext worldContext = new WorldContext();
}
