package be.kdg.ipj3.achievementProgress.domain;

import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record AchievementProgressId(
        UserId userId,
        AchievementId achievementId
) {
}
