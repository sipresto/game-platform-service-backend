package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record AchievementProgressId(
        UserId userId,
        AchievementId achievementId
) {
}
