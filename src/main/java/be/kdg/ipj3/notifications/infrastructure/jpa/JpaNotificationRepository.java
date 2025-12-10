package be.kdg.ipj3.notifications.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<JpaNotificationEntity, UUID> {
    @Query("SELECT n FROM JpaNotificationEntity n WHERE n.userId = :id")
    List<JpaNotificationEntity> findByUserId(UUID id);
}
