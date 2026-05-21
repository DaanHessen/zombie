package dev.daanh.zombie.location.locks;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CODE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodeLock extends Lock {
    private String code;

    private int codeLength;
}
