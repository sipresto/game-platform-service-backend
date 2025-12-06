package be.kdg.ipj3.shared.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.lobbies.domain.Difficulty;
import be.kdg.ipj3.sessions.domain.SessionResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameResultMessageDto(
        UUID gameId,
        List<UUID> players,
        UUID winnerId,
        Difficulty difficulty,
        SessionResult sessionResult,
        Instant completedAt
) {
}

