package dev.daanh.zombie.location.locks;

import dev.daanh.zombie.location.AccessProfile;

import dev.daanh.zombie.location.enums.AccessState;
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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "locks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "lock_type")
public abstract class Lock {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int difficulty;

    private boolean pickable;

    private int durability;

    @OneToOne(mappedBy = "lock")
    private AccessProfile accessProfile;

    public void breakLock(int damageAmount) {
        if (damageAmount <= 0) { return; }

        this.durability = Math.max(0, this.durability - damageAmount);

        if (this.durability <= 0) {
            this.accessProfile.setState(AccessState.COLLAPSED);
        }
    }

    public void openLock() {
        if (this.accessProfile.getState() == AccessState.OPEN) { return; }

        if (this.durability <= 0) { return; }

        this.accessProfile.setState(AccessState.OPEN);
    }

    public void closeLock() {
        if (this.accessProfile.getState() == AccessState.CLOSED) { return; }

        if (this.durability <= 0) { return; }

        this.accessProfile.setState(AccessState.CLOSED);
    }
}
