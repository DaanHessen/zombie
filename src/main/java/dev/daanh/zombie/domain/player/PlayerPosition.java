package dev.daanh.zombie.domain.player;

import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.world.Coordinates;
import dev.daanh.zombie.domain.world.Settlement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlayerPosition extends BaseState {
    @OneToOne(fetch = FetchType.LAZY)
    private Player player;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;
}
