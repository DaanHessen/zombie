package dev.daanh.zombie.domain.location.locks;

import dev.daanh.zombie.domain.location.enums.AccessState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;
import java.util.UUID;

@Entity
@DiscriminatorValue("KEY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyLock extends Lock {
    private UUID keyId;

    public void unlock(UUID keyId) {
        if (!this.accessProfile.isAccessible()) { return; }

        if (this.keyId.equals(keyId)) {
            super.openLock();
        }
    }
}
