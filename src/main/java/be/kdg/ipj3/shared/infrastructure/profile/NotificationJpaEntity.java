package be.kdg.ipj3.shared.infrastructure.profile;

import be.kdg.ipj3.shared.domain.profile.Notification;
import be.kdg.ipj3.shared.domain.profile.NotificationId;
import be.kdg.ipj3.shared.domain.profile.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationJpaEntity {
    @Id
    private UUID notificationId;
    private String description;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private Instant createdAt;
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileJpaEntity profile;

    public NotificationJpaEntity(UUID notificationId, String description, NotificationType type, Instant createdAt, boolean isRead, ProfileJpaEntity profile) {
        this.notificationId = notificationId;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.profile = profile;
    }

    public static NotificationJpaEntity fromDomain(Notification notification, ProfileJpaEntity profile) {
        return new NotificationJpaEntity(
                notification.id().notificationId(),
                notification.description(),
                notification.type(),
                notification.createdAt(),
                notification.isRead(),
                profile
        );
    }

    protected NotificationJpaEntity() {
    }

    public Notification toDomain() {
        return new Notification(
                new NotificationId(this.notificationId),
                this.description,
                this.type,
                this.createdAt,
                this.isRead
        );
    }
}
