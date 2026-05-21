package dev.daanh.zombie.world;

import dev.daanh.zombie.core.GameTime;
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

    @Setter(AccessLevel.NONE)
    private long seed;

    @Embedded
    @AttributeOverride(name = "ticks", column = @Column(name = "game_ticks"))
    private GameTime time = new GameTime();

    @OneToMany(mappedBy = "world", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Continent> continents = new ArrayList<>();

    public void advanceTime() {
        this.time.advanceTime(1);
    }
}
