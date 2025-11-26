package be.kdg.ipj3.sessions.infrastructure.messaging.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;

import java.time.Instant;
import java.util.UUID;

public record GameResultMessageDto(UUID gameId,
                                   UUID playerXId,
                                   UUID playerOId,
                                   UUID winnerId,
                                   Difficulty difficulty,
                                   String finalStatus,
                                   Instant completedAt
) {

}

