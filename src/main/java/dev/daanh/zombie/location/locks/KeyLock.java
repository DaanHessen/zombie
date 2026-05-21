package dev.daanh.zombie.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("KEY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyLock extends Lock {
    private UUID keyId;
}
