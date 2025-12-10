package be.kdg.ipj3.notifications.infrastructure.jpa;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class JpaNotificationEntity {
    @Id
    @Column(name = "notification_id")
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    private String description;

    private Instant createdAt;

    private boolean read;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    protected JpaNotificationEntity() {} // for JPA

    public JpaNotificationEntity(UUID id, UUID userId, String description, Instant createdAt, boolean read, NotificationType type) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.createdAt = createdAt;
        this.read = read;
        this.type = type;
    }

    public static JpaNotificationEntity fromDomain(Notification notification) {
        return new JpaNotificationEntity(
                notification.getId().id(),
                notification.getUser().id(),
                notification.getDescription(),
                notification.getCreatedAt(),
                notification.isRead(),
                notification.getType()
        );
    }

    public Notification toDomain() {
        return new Notification(
                new NotificationId(this.id),
                this.description,
                this.type,
                this.createdAt,
                this.read,
                new UserId(this.userId)
                );
    }
}
