package be.kdg.ipj3.shared.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.shared.SessionStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameResultMessageDto(
        UUID gameId,
        List<UUID> players,
        UUID winnerId,
        Difficulty difficulty,
        SessionStatus sessionStatus,
        Instant completedAt
) {
}

