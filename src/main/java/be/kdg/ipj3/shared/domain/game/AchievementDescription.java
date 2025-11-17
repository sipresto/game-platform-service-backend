package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;

public record AchievementDescription(@NotNull String description) {
    public AchievementDescription{
        if(description == null || description.isBlank() || description.length() < 20 || description.length() > 500)
            throw new IllegalArgumentException("achievement description cannot be empty and has to be between 20 and 500 characters");
    }
}
