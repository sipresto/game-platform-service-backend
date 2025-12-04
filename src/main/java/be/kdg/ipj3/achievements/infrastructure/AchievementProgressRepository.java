package be.kdg.ipj3.achievements.infrastructure;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.IAchievementProgressRepository;
import be.kdg.ipj3.achievements.infrastructure.jpa.JpaAchievementProgressEntity;
import be.kdg.ipj3.achievements.infrastructure.jpa.JpaAchievementProgressId;
import be.kdg.ipj3.achievements.infrastructure.jpa.JpaAchievementProgressRepository;
import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AchievementProgressRepository implements IAchievementProgressRepository {
    private final JpaAchievementProgressRepository jpaAchievementProgressRepository;

    public AchievementProgressRepository(JpaAchievementProgressRepository jpaAchievementProgressRepository) {
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
