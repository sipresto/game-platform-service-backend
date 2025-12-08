package be.kdg.ipj3.achievementProgress.domain;

import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.users.shared.UserId;

import java.util.Optional;

public interface AchievementProgressRepository {
    Optional<AchievementProgress> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId);

    void save(AchievementProgress progress);

    void deleteAll();
}
