package dev.daanh.zombie.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("KEYCARD")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeycardLock extends Lock {
    private String keycardId;

    private int accessLevel;

    private boolean requiresPower;
}
