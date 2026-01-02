package be.kdg.ipj3.shared.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameResultMessageDto(
        UUID gameId,
        List<String> players,
        String winnerId,
        Difficulty difficulty,
        String status,
        Instant completedAt,
        boolean endOfGame
) {
}

