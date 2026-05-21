package dev.daanh.zombie.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
