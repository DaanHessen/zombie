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
public class LootProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double foodChance;

    private double medicineChance;

    private double weaponChance;

    private double toolChance;

    private double fuelChance;

    private double clothingChance;

    private double junkChance;

    @OneToOne(mappedBy = "lootProfile")
    private Location location;
}
