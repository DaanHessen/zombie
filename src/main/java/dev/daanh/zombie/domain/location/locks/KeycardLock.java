package dev.daanh.zombie.domain.location.locks;

import dev.daanh.zombie.domain.location.enums.AccessState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("KEYCARD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycardLock extends Lock {
    private String keycardId;

    private int accessLevel;

    private boolean requiresPower;

    public void unlock(UUID keycardId, int accessLevel) {
        if (!this.accessProfile.isAccessable()) { return; }

        UUID keycardUUID = UUID.fromString(this.keycardId);

        if (keycardUUID.equals(keycardId) && this.accessLevel >= accessLevel) {
            super.openLock();
        }
    }
}
