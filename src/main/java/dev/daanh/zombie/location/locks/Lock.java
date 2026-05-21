package dev.daanh.zombie.location.locks;

import dev.daanh.zombie.location.AccessProfile;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "locks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "lock_type")
public abstract class Lock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int difficulty;

    private boolean pickable;

    private boolean breakable;

    private int durability;

    @OneToOne(mappedBy = "lock", fetch = FetchType.LAZY)
    private AccessProfile accessProfile;
}
