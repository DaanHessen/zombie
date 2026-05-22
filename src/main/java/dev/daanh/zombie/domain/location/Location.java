package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.location.enums.LocationCategory;
import dev.daanh.zombie.domain.location.enums.LocationCondition;
import dev.daanh.zombie.domain.location.enums.LocationSize;
import dev.daanh.zombie.domain.location.enums.LocationType;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.Region;
import dev.daanh.zombie.domain.world.Settlement;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    @Enumerated(EnumType.STRING)
    private LocationSize size;

    @Enumerated(EnumType.STRING)
    private LocationCategory category;

    @Enumerated(EnumType.STRING)
    private LocationCondition condition;

    @Embedded
    private Coordinates coordinates;

    private boolean indoors;

    private boolean enterable;

    private boolean searchable;

    private boolean generated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

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

    public void search() {
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
}
