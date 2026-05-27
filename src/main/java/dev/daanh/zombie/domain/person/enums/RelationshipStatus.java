package dev.daanh.zombie.domain.person.enums;

public enum RelationshipStatus {
    TRUSTED,    // High score (e.g. > 50)
    NEUTRAL,    // Average score (e.g. -50 to 50)
    ENEMY       // Low score (e.g. < -50)
}
