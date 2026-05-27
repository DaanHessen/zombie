package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.survivor.enums.RelationshipLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RelationshipLabel label;

    private int trustScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_survivor_id")
    private Survivor targetSurvivor;
}
