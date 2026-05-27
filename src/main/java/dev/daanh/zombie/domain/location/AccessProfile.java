package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.location.enums.AccessState;
import dev.daanh.zombie.domain.location.locks.Lock;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessProfile {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AccessState state;

    // keepin it simple here for MVP. YARGNI!!!!!!!!!
//    private boolean requiresTool;

//    private boolean requiresClimbing;

//    private boolean requiresSwimming;

    @OneToOne(mappedBy = "accessProfile")
    private LocationState locationState;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lock_id")
    private Lock lock;

    public boolean isAccessible() {
        if (this.state == AccessState.BARRICADED ||
                this.state == AccessState.BLOCKED ||
                this.state == AccessState.COLLAPSED ||
                this.state == AccessState.LOCKED) {
            return false;
        }

        return true;
    }
}
