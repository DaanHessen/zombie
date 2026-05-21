package dev.daanh.zombie.location;

import dev.daanh.zombie.world.Region;
import dev.daanh.zombie.world.Settlement;

import dev.daanh.zombie.location.enums.LocationCategory;
import dev.daanh.zombie.location.enums.LocationCondition;
import dev.daanh.zombie.location.enums.LocationSize;
import dev.daanh.zombie.location.enums.LocationType;
import dev.daanh.zombie.world.Coordinates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    private boolean indoors;

    private boolean enterable;

    private boolean searchable;

    private boolean generated;

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
}
