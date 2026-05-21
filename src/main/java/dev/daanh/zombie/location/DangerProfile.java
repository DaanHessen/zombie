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
public class DangerProfile {
    @Id
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
