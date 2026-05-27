package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.domain.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class SettlementState extends BaseEntity {

    @Column(name = "settlement_id", nullable = false)
    private UUID settlementId;

    @Column(name = "is_ground_zero")
    private Boolean isGroundZero = false;

    private int population;
}
