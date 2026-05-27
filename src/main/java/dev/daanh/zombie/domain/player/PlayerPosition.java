package dev.daanh.zombie.domain.player;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.Settlement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlayerPosition extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private Player player;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "location_id")
    private java.util.UUID locationId;

    @Column(name = "settlement_id")
    private java.util.UUID settlementId;
}
