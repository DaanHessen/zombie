package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.player.Player;
import dev.daanh.zombie.domain.player.PlayerPosition;
import dev.daanh.zombie.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PlayerService {
    private PlayerRepository playerRepository;

    public PlayerPosition getPlayerPosition(UUID playerId) {
        return playerRepository.findById(playerId)
                .map(Player::getPosition)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
    }
}
