package be.kdg.ipj3.notifications.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserNotificationPreferencesRepository extends JpaRepository<JpaUserNotificationPreferencesEntity, UUID> {
    Optional<JpaUserNotificationPreferencesEntity> findByUserId(UUID id);
}