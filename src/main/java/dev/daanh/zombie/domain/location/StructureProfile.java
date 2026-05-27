package dev.daanh.zombie.domain.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StructureProfile {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int floors;

    private int rooms;

    private boolean hasBasement;

    private boolean hasRoofAccess;

    private boolean hasPower;

    private boolean hasWater;

    @OneToOne(mappedBy = "structureProfile")
    private LocationState locationState;
}
