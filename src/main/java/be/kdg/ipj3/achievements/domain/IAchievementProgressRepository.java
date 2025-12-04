package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;

import java.util.Optional;

public interface IAchievementProgressRepository {
    Optional<AchievementProgress> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId);

    void save(AchievementProgress progress);

    void deleteAll();
}
