package be.kdg.ipj3.achievementProgress.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAchievementProgressRepository extends JpaRepository<JpaAchievementProgressEntity, JpaAchievementProgressId> {
}
