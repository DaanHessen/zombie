package dev.daanh.zombie.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@DiscriminatorValue("KEY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeyLock extends Lock {
    private UUID keyId;
}
