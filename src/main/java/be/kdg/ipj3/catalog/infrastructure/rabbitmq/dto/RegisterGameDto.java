package be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record RegisterGameDto(
        @NotNull UUID id,
        @NotBlank String name,
        String description,
        BigDecimal price,
        @NotBlank String backendUrl,
        @NotBlank String frontendUrl,
        @Size(max = 2000) String rules,
        List<RegisterAchievementDto> achievements,
        List<String> categories,
        String imageUrl,
        @NotNull Instant timestamp,
        @NotNull int maxPlayers
) {
    public Game toDomain() {
        return new Game(
                new GameId(id),
                name,
                description,
                Money.of(price),
                backendUrl,
                frontendUrl,
                rules,
                achievements.stream()
                        .map(RegisterAchievementDto::toDomain)
                        .toList(),
                categories.stream()
                        .map(Category::fromStringOrNull)
                        .filter(Objects::nonNull)
                        .toList(),
                imageUrl,
                timestamp,
                maxPlayers
        );
    }
}
