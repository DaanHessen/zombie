package dev.daanh.zombie.domain.world;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country {
    
    @Id
    @Column(length = 2)
    private String id;
    
    private String name;

    @Enumerated(EnumType.STRING)
    private CivilianFirearmOwnership civilianFirearmOwnership;

    @Enumerated(EnumType.STRING)
    private MedicalInfrastructure medicalInfrastructure;

    @Enumerated(EnumType.STRING)
    private MilitaryArchetype militaryArchetype;

    @Enumerated(EnumType.STRING)
    private PoliceMilitarization policeMilitarization;

    @Enumerated(EnumType.STRING)
    private MilitaryDoctrine militaryDoctrine;

    @Enumerated(EnumType.STRING)
    private ContainmentStrategy containmentStrategy;

    @Enumerated(EnumType.STRING)
    private PowerGridResilience powerGridResilience;

    private double corruptionIndex;
    private double trustInGovernment;
    private double culturalIndividualism;

    public enum CivilianFirearmOwnership { NONE, RESTRICTED, MODERATE, HIGH, UBIQUITOUS }
    public enum MedicalInfrastructure { POOR, DEVELOPING, ADVANCED, WORLD_CLASS }
    public enum MilitaryArchetype { DEVELOPING_SURPLUS, EASTERN_BLOC, NATO_STANDARD }
    public enum PoliceMilitarization { UNARMED_WESTERN, ARMED_WESTERN, MILITARIZED, PARAMILITARY }
    public enum MilitaryDoctrine { DEFENSIVE_GARRISON, EXPEDITIONARY, ASYMMETRIC, CONSTABULARY }
    public enum ContainmentStrategy { DENIAL, QUARANTINE_ZONES, MASS_EVACUATION, AGGRESSIVE_CULLING }
    public enum PowerGridResilience { FRAGILE, STANDARD, ROBUST, ISOLATED_MICROGRIDS }
}
