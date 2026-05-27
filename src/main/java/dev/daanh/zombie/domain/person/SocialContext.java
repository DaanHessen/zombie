package dev.daanh.zombie.domain.person;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.person.social.Group;
import dev.daanh.zombie.domain.person.social.Relationship;
import dev.daanh.zombie.domain.person.enums.GroupRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
public class SocialContext extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id")
    @JoinColumn(name = "social_context_id")
    private Map<UUID, Relationship> relationships;

    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;

    private int influence;
}
