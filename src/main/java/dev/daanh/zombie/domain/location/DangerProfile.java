package dev.daanh.zombie.domain.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DangerProfile {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double zombieRisk;

    private double humanRisk;

    private double animalRisk;

    private double diseaseRisk;

    private double structuralRisk;

    private double fireRisk;

    @OneToOne(mappedBy = "dangerProfile")
    private Location location;
}
