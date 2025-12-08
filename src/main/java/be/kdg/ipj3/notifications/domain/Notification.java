package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@AggregateRoot
public class Notification {
    @Identity
    private final NotificationId id;
    private final String description;
    private final NotificationType type;
    private final Instant createdAt;
    private boolean read;
    private final UserId user;

    public Notification(NotificationId id, String description, NotificationType type, Instant createdAt, boolean read, UserId user) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
        this.user = user;
    }

    public Notification(String description, NotificationType type, UserId user) {
        this(NotificationId.create(), description, type, Instant.now(), false, user);
    }

    public NotificationId getId() {
        return id;
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

    public void read() {
        this.read = true;
    }
}
