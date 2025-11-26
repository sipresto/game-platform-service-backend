package be.kdg.ipj3.sessions.infrastructure.messaging.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.Session;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record GameResultMessageDto(UUID gameId,
                                   UUID playerXId,
                                   UUID playerOId,
                                   UUID winnerId,
                                   Difficulty difficulty,
                                   String finalStatus,
                                   Instant completedAt
) {
    public Session toDomain() {
        GameId game = new GameId(gameId);
        List<UserId> players = new ArrayList<>();
        if (playerXId != null) {
            players.add(new UserId(playerXId));
        }
        if (playerOId != null) {
            players.add(new UserId(playerOId));
        }

        UserId winner = winnerId != null ? new UserId(winnerId) : null;
        SessionResult result = SessionResult.valueOf(finalStatus);

        return new Session(game, players, difficulty, winner, result);
    };
}

