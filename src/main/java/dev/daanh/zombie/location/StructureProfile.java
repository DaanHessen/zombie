package dev.daanh.zombie.location;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StructureProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int floors;

    private int rooms;

    private boolean hasBasement;

    private boolean hasRoofAccess;

    private boolean hasPower;

    private boolean hasWater;

    // vague so I might change it later (fortification / base builder package for example)
    private int fortificationPotential;

    @OneToOne(mappedBy = "structureProfile")
    private Location location;
}
