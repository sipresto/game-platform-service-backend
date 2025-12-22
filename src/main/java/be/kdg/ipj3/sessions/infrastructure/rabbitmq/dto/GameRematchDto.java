package be.kdg.ipj3.sessions.infrastructure.rabbitmq.dto;

import java.util.UUID;

public record GameRematchDto(
        UUID oldGameId,
        UUID newGameId
) {
}
