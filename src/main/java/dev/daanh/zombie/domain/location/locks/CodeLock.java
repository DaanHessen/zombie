package dev.daanh.zombie.domain.location.locks;

import dev.daanh.zombie.domain.location.enums.AccessState;
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

    public void unlock(String code) {
        if (!this.accessProfile.isAccessable()) { return; }

        if (this.code.equals(code) && this.codeLength == code.length()) {
            super.openLock();
        }
    }
}
