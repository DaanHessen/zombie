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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessProfile {
    @Id
    @Setter(AccessLevel.NONE)
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
