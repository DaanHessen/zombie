package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.location.enums.AccessState;
import dev.daanh.zombie.domain.location.enums.LocationSize;
import dev.daanh.zombie.domain.location.enums.LocationType;
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
public class LootProfile {
    @Id
    @Setter(AccessLevel.NONE)
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
    private LocationState locationState;
}
