package be.kdg.ipj3.achievementProgress.shared;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.event.annotation.DomainEvent;

import java.time.Instant;

@DomainEvent
public record AchievementEarnedEvent(
        UserId playerId,
        String description,
        Instant earnedAt
) {}
