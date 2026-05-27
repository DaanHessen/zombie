package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.location.enums.LocationCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class LocationState extends BaseEntity {

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loot_profile_id")
    private LootProfile lootProfile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "danger_profile_id")
    private DangerProfile dangerProfile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "access_profile_id")
    private AccessProfile accessProfile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "structure_profile_id")
    private StructureProfile structureProfile;

    @Enumerated(EnumType.STRING)
    private LocationCondition condition;

    private boolean enterable;

    private boolean searchable;

    public void search() {
        if (!this.enterable) { return; }
        if (!this.searchable) { return; }

        this.searchable = false;
    }

    public void degradeCondition() {
        if (this.condition == null) { return; }

        if (this.condition == LocationCondition.INTACT) {
            this.condition = LocationCondition.WORN;
        } else if (this.condition == LocationCondition.WORN) {
            this.condition = LocationCondition.DAMAGED;
        }
    }

    public boolean hasAccess() {
        return this.accessProfile != null && this.accessProfile.isAccessible();
    }
}
