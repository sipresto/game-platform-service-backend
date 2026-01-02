package be.kdg.ipj3.sessions.infrastructure.rabbitmq.dto;

import java.util.List;
import java.util.UUID;

public record SessionStartDto(
        UUID gameId,
        UUID sessionId,
        List<UUID> playerIds
) {

}
