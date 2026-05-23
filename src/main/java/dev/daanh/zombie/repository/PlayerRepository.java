package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
