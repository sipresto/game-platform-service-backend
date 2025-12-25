package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.UUID;

@AggregateRoot
public class Notification {
    @Identity
    private final NotificationId id;
    private final String title;
    private final String description;
    private final NotificationType type;
    private final Instant createdAt;
    private boolean read;
    private final UserId user;
    private final UUID referenceId;

    public Notification(NotificationId id, String title, String description, NotificationType type,
                        Instant createdAt, boolean read, UserId user, UUID referenceId) {
        this.id = id;
        this.title = title == null ? generateTitle(type) : title;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
        this.user = user;
        this.referenceId = referenceId;
    }

    public Notification(String description, NotificationType type, UserId user) {
        this(NotificationId.create(), null, description, type, Instant.now(), false, user, null);
    }

    public Notification(String title, String description, NotificationType type, Instant sentAt, UserId user, UUID referenceId) {
        this(NotificationId.create(), title, description, type, sentAt, false, user, referenceId);
    }

    public NotificationId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public NotificationType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public UserId getUser() {
        return user;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void read() {
        this.read = true;
    }

    private String generateTitle(NotificationType type) {
        return switch (type) {
            case FRIEND_REQUEST -> "You have a new friend request!";
            case ACHIEVEMENT -> "New achievement unlocked!";
            case YOUR_TURN -> "Your turn!";
            default -> "Notification";
        };
    }
}