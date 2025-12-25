package be.kdg.ipj3.notifications.infrastructure.jpa;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.shared.NotificationType;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean read;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private UUID referenceId;

    protected JpaNotificationEntity() {
    } // for JPA

    public JpaNotificationEntity(UUID id, UUID userId, String title, String description, Instant createdAt,
                                 boolean read, NotificationType type, UUID referenceId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.read = read;
        this.type = type;
        this.referenceId = referenceId;
    }

    public static JpaNotificationEntity fromDomain(Notification notification) {
        return new JpaNotificationEntity(
                notification.getId().id(),
                notification.getUser().id(),
                notification.getTitle(),
                notification.getDescription(),
                notification.getCreatedAt(),
                notification.isRead(),
                notification.getType(),
                notification.getReferenceId()
        );
    }

    public Notification toDomain() {
        return new Notification(
                new NotificationId(this.id),
                this.title,
                this.description,
                this.type,
                this.createdAt,
                this.read,
                new UserId(this.userId),
                this.referenceId
        );
    }
}
