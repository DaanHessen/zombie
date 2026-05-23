package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.domain.core.BaseState;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SettlementState extends BaseState {

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    private int population;
}
