package dev.daanh.zombie.location;

import dev.daanh.zombie.location.locks.Lock;
import dev.daanh.zombie.location.enums.AccessState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AccessState state;

    private boolean requiresTool;

    private boolean requiresClimbing;

    private boolean requiresSwimming;

    @OneToOne(mappedBy = "accessProfile")
    private Location location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lock_id")
    private Lock lock;
}
