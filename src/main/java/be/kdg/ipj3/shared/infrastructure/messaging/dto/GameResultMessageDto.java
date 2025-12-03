package be.kdg.ipj3.shared.infrastructure.messaging.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.SessionResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GameResultMessageDto(
        UUID gameId,
        List<UUID> players,
        UUID winnerId,
        Difficulty difficulty,
        SessionResult sessionResult,
        Instant completedAt,
        Map<String, Map<String, Object>> metadata
) {
}

