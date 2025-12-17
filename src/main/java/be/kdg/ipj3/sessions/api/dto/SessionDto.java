package be.kdg.ipj3.sessions.api.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SessionDto(
        @NotNull UUID id,
        @NotNull UUID gameId,
        @NotNull List<UUID> playerIds,
        Difficulty difficulty,
        UUID winnerId,
        SessionStatus result,
        @NotNull Instant completedAt
) {
    public static SessionDto fromDomain(Session session) {
        return new SessionDto(
                session.getId().id(),
                session.getGame().id(),
                session.getPlayers().stream()
                        .map(UserId::id)
                        .toList(),
                session.getDifficulty(),
                session.getWinner() == null
                        ? null
                        : session.getWinner().id(),
                session.getStatus(),
                session.getCompletedAt()
        );
    }
}
