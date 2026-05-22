package dev.daanh.zombie.domain.world;

import dev.daanh.zombie.domain.world.enums.WaterBodyType;

import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterBody {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private WaterBodyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id")
    private World world;
}
