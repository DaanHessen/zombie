package dev.daanh.zombie.domain.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CODE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeLock extends Lock {
    @Setter(AccessLevel.NONE)
    private String code;

    private int codeLength;
}
