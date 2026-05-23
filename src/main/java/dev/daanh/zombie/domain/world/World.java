package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.core.GameTime;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class World {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private long seed;

    @Embedded
    @AttributeOverride(name = "ticks", column = @Column(name = "game_ticks"))
    private GameTime time = new GameTime();

    public void advanceTime() {
        this.time = this.time.plus(GameConfig.getMinutesPerTick());
    }
}
