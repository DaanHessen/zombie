package dev.daanh.zombie.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
