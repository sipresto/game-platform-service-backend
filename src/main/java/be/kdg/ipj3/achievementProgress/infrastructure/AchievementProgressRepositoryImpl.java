package be.kdg.ipj3.achievementProgress.infrastructure;

import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.achievementProgress.infrastructure.jpa.JpaAchievementProgressEntity;
import be.kdg.ipj3.achievementProgress.infrastructure.jpa.JpaAchievementProgressId;
import be.kdg.ipj3.achievementProgress.infrastructure.jpa.JpaAchievementProgressRepository;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AchievementProgressRepositoryImpl implements AchievementProgressRepository {
    private final JpaAchievementProgressRepository jpaAchievementProgressRepository;

    public AchievementProgressRepositoryImpl(JpaAchievementProgressRepository jpaAchievementProgressRepository) {
        this.jpaAchievementProgressRepository = jpaAchievementProgressRepository;
    }

    @Override
    public Optional<AchievementProgress> findByUserIdAndAchievementId(UserId userId, AchievementId achievementId) {
        return jpaAchievementProgressRepository.findById(new JpaAchievementProgressId(userId.id(), achievementId.id()))
                .map(JpaAchievementProgressEntity::toDomain);
    }

    @Override
    public void save(AchievementProgress progress) {
        jpaAchievementProgressRepository.save(JpaAchievementProgressEntity.fromDomain(progress));
    }

    @Override
    public void deleteAll() {
        jpaAchievementProgressRepository.deleteAll();
    }
}
