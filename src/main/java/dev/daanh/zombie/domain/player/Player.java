package dev.daanh.zombie.domain.player;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerPosition position;
}
